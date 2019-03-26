package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.message.PutChunkMessage;
import proj.peer.message.handlers.async.BackupChunkHandler;
import proj.peer.utils.SHA256Encoder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Sends a file through a multicast connection.
 */
public class FileSender {

    /**
     * Maximum size of a chunk.
     */
    private static final int CHUNK_SIZE = 64000;

    /**
     * Peer associated with the sender.
     */
    private Peer peer;

    /**
     * Path to the file.
     */
    private String pathname;

    /**
     * Replication degree of the file.
     */
    private Integer replicationDegree;

    /**
     * Saves all handlers for the messages sent.
     */
    private ArrayList<BackupChunkHandler> handlers;

    /**
     * Latch that waits fot all handlers to conclude.
     */
    private CountDownLatch chunkSavedSignal;
    private File file;


    /**
     * Sends a file via a multicast connection.
     * @param peer Peer associated with the sender.
     * @param pathname Path to the file.
     * @param replicationDegree Replication degree of the file.
     */
    FileSender(Peer peer, String pathname, Integer replicationDegree) {
        this.peer = peer;
        this.pathname = pathname;
        this.replicationDegree = replicationDegree;
        this.handlers = new ArrayList<>();
        this.file = new File(pathname);
    }

    /**
     * Initiates the sending and subscription of a PUTCHUNK message.
     * @param replicationDegree Replication degree of the message.
     * @param encodedFileName Encoded name of the file.
     * @param body Body of the message.
     * @param chunkNo Chunk number of the message.
     * @return Handler for the message subscription and retransmission.
     */
    private BackupChunkHandler sendChunk(Integer replicationDegree, String encodedFileName, String body, int chunkNo) {
        PutChunkMessage msg = new PutChunkMessage(peer.getVersion(), peer.getPeerId(), encodedFileName, chunkNo, replicationDegree, body);
        BackupChunkHandler handler = new BackupChunkHandler(this.peer, msg, this.chunkSavedSignal);
        handler.run();
        this.peer.getControl().subscribe(handler);
        return handler;
    }

    /**
     * Initiates the transfer of file.
     * @return True if the transfer was started successfully.
     */
    boolean sendFile() {

        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[CHUNK_SIZE];
            double nChunks = data.length() / (double) CHUNK_SIZE;
            String encodedFileName = SHA256Encoder.encode(file.getName());

            this.chunkSavedSignal = new CountDownLatch((int) nChunks + ((nChunks == Math.floor(nChunks)) ? 1 : 0));

            int i;
            for (i = 0; i < nChunks; i++) {
                data.read(buffer);
                this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, new String(buffer, 0, buffer.length), i));
            }

            if (nChunks == Math.floor(nChunks)) {
                this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, "", i));
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Waits for the file transfer to end.
     * @return True if the transfer was successful.
     */
    boolean waitOperation() {
        if (chunkSavedSignal == null)
            return true;

        try {
            this.chunkSavedSignal.await();
        } catch (InterruptedException e) {
            return false;
        }


        for (BackupChunkHandler handler : this.handlers) {
            if (!handler.wasSuccessful()) {
                return false;
            }
        }
        return true;
    }

}