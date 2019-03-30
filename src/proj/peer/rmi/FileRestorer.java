package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.handlers.async.ChunkMsgHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.operations.SaveChunkOperation;
import proj.peer.utils.SHA256Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.CountDownLatch;
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

        SaveChunkOperation chunkSaver = null;
        Thread savingThread = null;
        try {
            chunkSaver = new SaveChunkOperation(new FileOutputStream(fileFolder.getAbsolutePath() + "/" + filename));
            savingThread = new Thread(chunkSaver);
            savingThread.start();
            for (int i = 0; ; i++) {
                byte[] body = restoreChunk(filename, i);
                chunkSaver.addChunk(body);
                if (body.length < MulticastConnection.CHUNK_SIZE) {
                    break;
                }
            }

        } catch (Exception e) {
            if (savingThread != null) {
                savingThread.interrupt();
            }
            NetworkLogger.printLog(Level.SEVERE, "Error Restoring File - " + e.getMessage());
            return false;
        }
        return true;
    }

    private byte[] restoreChunk(String filename, Integer chunkNo) throws Exception {
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
