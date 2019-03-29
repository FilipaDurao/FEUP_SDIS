package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.message.handlers.async.ChunkMsgHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.utils.SHA256Encoder;

import java.io.*;
import java.util.concurrent.CountDownLatch;

public class FileRestorer {
    /**
     * Peer associated with the sender.
     */
    private Peer peer;

    private String restorePath;
    private final File fileFolder;

    FileRestorer(Peer peer) {
        this.peer = peer;
        this.restorePath = "data/restore_" + this.peer.getPeerId() + "/";
        fileFolder = new File(this.restorePath);
        fileFolder.mkdirs();
    }

    public boolean restoreFile(String filename) {

        try (FileOutputStream stream = new FileOutputStream(fileFolder.getAbsolutePath() + "/" + filename)) {

            for (int i = 0;;i++) {
                byte[] body = restoreChunk(filename, i);
                System.out.println("Received CHUNK with body length: " + body.length);
                stream.write(body);
                if (body.length < MulticastConnection.CHUNK_SIZE) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private byte[] restoreChunk(String filename, Integer chunkNo) throws Exception  {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getVersion(), this.peer.getPeerId(), SHA256Encoder.encode(filename), chunkNo);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ChunkMsgHandler handler = new ChunkMsgHandler(peer, msg, countDownLatch);
        this.peer.getRestore().subscribe(handler);
        handler.run();
        countDownLatch.await();
        if (!handler.wasSuccessful())
            throw new Exception("Chunk retrieval not successful.");

        return handler.getBody();
    }
}
