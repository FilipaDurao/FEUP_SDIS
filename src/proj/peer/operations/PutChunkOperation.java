package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.utils.RandomGenerator;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PutChunkOperation implements Runnable {

    private PutChunkMessage msg;
    private Peer peer;

    public PutChunkOperation(PutChunkMessage msg, Peer peer) {
        this.msg = msg;
        this.peer = peer;
    }

    @Override
    public void run() {
        try {
            this.peer.getFileManager().putChunk(msg.getFileId(), msg.getChunkNo(), msg.getBody(), msg.getReplicationDegree());
            StoredMessage response = new StoredMessage(peer.getPeerId(), msg.getFileId(), msg.getChunkNo());
            int delay = RandomGenerator.getNumberInRange(0, 400);
            this.peer.getScheduler().schedule(new SendMessageOperation(peer.getControl(), response), delay, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "PUTCHUNK operation - " + e.getMessage());
        }
    }
}
