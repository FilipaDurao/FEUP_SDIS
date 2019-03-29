package proj.peer.message.handlers.async;

import proj.peer.Peer;
import proj.peer.message.MessageSender;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.utils.RandomGenerator;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PutChunkHandler implements Runnable {

    private PutChunkMessage msg;
    private Peer peer;

    public PutChunkHandler(PutChunkMessage msg, Peer peer) {
        this.msg = msg;
        this.peer = peer;
    }

    @Override
    public void run() {
        try {
            this.peer.getFileManager().putChunk(msg.getFileId(), msg.getChunkNo(), msg.getBody(), msg.getReplicationDegree());
            StoredMessage response = new StoredMessage(peer.getVersion(), peer.getPeerId(), msg.getFileId(), msg.getChunkNo());
            int delay = RandomGenerator.getNumberInRange(0, 400);
            this.peer.getScheduler().schedule(new MessageSender(peer.getControl(), response), delay, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            System.out.println("Error in PUTCHUNK operation: " + e.getMessage());
        }
    }
}
