package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.utils.RandomGenerator;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GetChunkOperation implements Runnable {

    private GetChunkMessage msg;
    private Peer peer;

    public GetChunkOperation(GetChunkMessage msg, Peer peer) {
        this.msg = msg;
        this.peer = peer;
    }

    @Override
    public void run() {
        try {
            if (this.peer.getFileManager().isChunkSaved(msg.getFileId(), msg.getChunkNo())) {
                byte[] body = this.peer.getFileManager().getChunk(msg.getFileId(), msg.getChunkNo());
                ChunkMessage response = new ChunkMessage(peer.getVersion(), peer.getPeerId(), msg.getFileId(), msg.getChunkNo(), body);
                int delay = RandomGenerator.getNumberInRange(0, 400);
                this.peer.getScheduler().schedule(new SendMessageOperation(peer.getRestore(), response), delay, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Failure sending chunk - " + e.getMessage());
        }
    }
}
