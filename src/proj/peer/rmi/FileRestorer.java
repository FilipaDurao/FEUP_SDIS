package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.message.handlers.async.ChunkHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.utils.SHA256Encoder;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class FileRestorer {
    /**
     * Peer associated with the sender.
     */
    private Peer peer;

    FileRestorer(Peer peer) {
        this.peer = peer;
    }

    public boolean restoreFile(String filename) {
        try {

            for (int i = 0;;i++) {
                String body = restoreChunk(filename, i);
                System.out.println("Received chunk no." + i + " with size " + body.length());
                if (body.length() < MulticastConnection.CHUNK_SIZE) {
                    break;
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String restoreChunk(String filename, Integer chunkNo) throws Exception  {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getVersion(), this.peer.getPeerId(), SHA256Encoder.encode(filename), chunkNo);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ChunkHandler handler = new ChunkHandler(peer, msg, countDownLatch);
        this.peer.getRestore().subscribe(handler);
        handler.run();
        countDownLatch.await();
        if (!handler.wasSuccessful())
            throw new Exception("Chunk retrieval not successful.");

        return handler.getBody();
    }
}
