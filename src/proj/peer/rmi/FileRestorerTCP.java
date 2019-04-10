package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.handlers.async.ChunkInitiatorHandler;
import proj.peer.handlers.async.ChunkInitiatorTCPHandler;
import proj.peer.message.messages.GetChunkMessage;

import java.util.concurrent.CountDownLatch;

public class FileRestorerTCP extends FileRestorer {
    FileRestorerTCP(Peer peer) {
        super(peer);
    }

    @Override
    protected byte[] initiateRestoreChunk(Integer chunkNo, String encode) throws Exception {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getVersion(), this.peer.getPeerId(), encode, chunkNo);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ChunkInitiatorHandler handler = new ChunkInitiatorTCPHandler(peer, msg, countDownLatch);
        return restoreChunk(countDownLatch, handler, this.peer.getRestore());
    }


}
