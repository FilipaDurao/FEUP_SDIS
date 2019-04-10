package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.handlers.async.ChunkInitiatorHandler;
import proj.peer.handlers.async.ChunkInitiatorTCPHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.operations.SaveChunkOperation;


public class FileRestorerTCP extends FileRestorer {
    FileRestorerTCP(Peer peer) {
        super(peer);
    }

    protected void initiateRestoreChunk(Integer chunkNo, String encode, SaveChunkOperation chunkSaver) throws Exception {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getVersion(), this.peer.getPeerId(), encode, chunkNo);
        ChunkInitiatorHandler handler = new ChunkInitiatorTCPHandler(peer, msg, chunkSaver, null);
        this.peer.getRestore().subscribe(handler);
        handler.startAsync();
    }


}
