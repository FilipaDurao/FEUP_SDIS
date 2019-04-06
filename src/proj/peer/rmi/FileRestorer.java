package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.handlers.async.ChunkInitiatorHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.operations.SaveChunkOperation;
import proj.peer.utils.SHA256Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class FileRestorer {
    /**
     * Peer associated with the sender.
     */
    private Peer peer;

    private String restorePath;
    private final File fileFolder;

    FileRestorer(Peer peer) {
        this.peer = peer;
        this.restorePath = "data/peer_" + this.peer.getPeerId() + "/restore/";
        fileFolder = new File(this.restorePath);
        fileFolder.mkdirs();
    }

    public boolean restoreFile(String filename) {

        SaveChunkOperation chunkSaver;
        Future saveChunkFuture = null;
        FileOutputStream stream = null;
        try {
            String encodedFilename = SHA256Encoder.encode(filename);
            stream = new FileOutputStream(fileFolder.getAbsolutePath() + "/" + filename);
            chunkSaver = new SaveChunkOperation(stream);
            saveChunkFuture = this.peer.getScheduler().schedule(chunkSaver, 0, TimeUnit.SECONDS);
            for (int i = 0; ; i++) {
                byte[] body;
                if (this.peer.getFileManager().isChunkSaved(encodedFilename, i)) {
                    body = this.peer.getFileManager().getChunk(encodedFilename, i);
                } else {
                    body = restoreChunk(i, encodedFilename);
                }

                if (body == null) {
                    throw new Exception("Body not found");
                }
                chunkSaver.addChunk(body);
                if (body.length < MulticastConnection.CHUNK_SIZE) {
                    break;
                }
            }

        } catch (Exception e) {
            if (saveChunkFuture != null) {
                saveChunkFuture.cancel(true);
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    NetworkLogger.printLog(Level.WARNING, "Error closing output stream - " + e1.getMessage());
                }
            }
            NetworkLogger.printLog(Level.SEVERE, "Error Restoring File - " + e.getMessage());
            return false;
        }
        return true;
    }

    private byte[] restoreChunk(Integer chunkNo, String encode) throws Exception {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getPeerId(), encode, chunkNo);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ChunkInitiatorHandler handler = new ChunkInitiatorHandler(peer, msg, countDownLatch);
        this.peer.getRestore().subscribe(handler);
        handler.run();
        countDownLatch.await();
        if (!handler.wasSuccessful())
            throw new Exception("Chunk retrieval not successful.");

        return handler.getBody();
    }
}
