package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.connection.RestoreConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.handlers.async.ChunkInitiatorHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.operations.SaveChunkOperation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class FileRestorer {
    public static final int WINDOW_SIZE = 5;
    /**
     * Peer associated with the sender.
     */
    protected Peer peer;

    protected String restorePath;
    protected File fileFolder;

    FileRestorer(Peer peer) {
        this.peer = peer;
        this.restorePath = "data/peer_" + this.peer.getPeerId() + "/restore/";
        fileFolder = new File(this.restorePath);
        fileFolder.mkdirs();
    }

    public boolean restoreFile(String filename, String encodedFilename) {

        SaveChunkOperation chunkSaver;
        Future saveChunkFuture = null;
        RandomAccessFile stream = null;
        try {
            int nChunks = this.peer.getFileManager().getRemoteNChunks(encodedFilename);
            stream = new RandomAccessFile(fileFolder.getAbsolutePath() + "/" + filename, "rw");
            chunkSaver = new SaveChunkOperation(stream, nChunks);
            saveChunkFuture = this.peer.getScheduler().schedule(chunkSaver, 0, TimeUnit.SECONDS);
            CountDownLatch latch = null;
            for (int i = 0; i < nChunks ; i++) {
                if(i % WINDOW_SIZE == 0) {
                    if (latch != null)
                        latch.await();
                    latch = new CountDownLatch(WINDOW_SIZE);
                }
                initiateRestoreChunk(i, encodedFilename, chunkSaver, latch);
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

    protected void initiateRestoreChunk(Integer chunkNo, String encode, SaveChunkOperation chunkSaver, CountDownLatch countDownLatch) throws Exception {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getPeerId(), encode, chunkNo);
        ChunkInitiatorHandler handler = new ChunkInitiatorHandler(peer, msg, chunkSaver, countDownLatch);
        this.peer.getRestore().subscribe(handler);
        handler.startAsync();
    }

}
