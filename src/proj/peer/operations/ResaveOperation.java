package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.handlers.async.StoredInitiatorHandler;
import proj.peer.log.NetworkLogger;
import proj.peer.manager.ChunkInfo;
import proj.peer.message.messages.PutChunkMessage;

import java.util.logging.Level;

public class ResaveOperation implements Runnable {

    private Peer peer;
    private String fileId;
    private Integer chunkNo;

    public ResaveOperation(Peer peer, String fileId, Integer chunkNo) {
        this.peer = peer;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {
        try {
            if (this.peer.getFileManager().isChunkSaved(this.fileId, this.chunkNo)) {
                ChunkInfo chunkInfo = this.peer.getFileManager().getChunkInfo(this.fileId, this.chunkNo);
                if (chunkInfo.getNumberOfSaves() < chunkInfo.getReplicationDegree()) {
                    byte[] body = this.peer.getFileManager().getChunk(this.fileId, this.chunkNo);
                    PutChunkMessage msg = new PutChunkMessage(peer.getPeerId(), this.fileId, chunkNo, chunkInfo.getReplicationDegree(), body);
                    StoredInitiatorHandler handler = new StoredInitiatorHandler(this.peer, msg, null);
                    handler.run();
                    this.peer.getControl().subscribe(handler);
                }

            }
        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Failed resave operation - " + e.getMessage());
        }
    }
}
