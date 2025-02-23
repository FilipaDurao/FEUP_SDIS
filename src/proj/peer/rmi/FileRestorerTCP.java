package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.handlers.async.ChunkInitiatorHandler;
import proj.peer.handlers.async.ChunkInitiatorTCPHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.operations.SaveFileOperation;

import java.util.concurrent.CountDownLatch;


public class FileRestorerTCP extends FileRestorer {
    FileRestorerTCP(Peer peer) {
        super(peer);
    }

    protected ChunkInitiatorHandler initiateRestoreChunk(Integer chunkNo, String encode, SaveFileOperation chunkSaver, CountDownLatch latch) throws Exception {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getVersion(), this.peer.getPeerId(), encode, chunkNo);
        ChunkInitiatorHandler handler = new ChunkInitiatorTCPHandler(peer, msg, chunkSaver, latch);
        this.peer.getRestore().subscribe(handler);
        handler.startAsync();
        return handler;
    }


}
