package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.handlers.async.StoredInitiatorHandler;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.utils.SHA256Encoder;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**
 * Sends a file through a multicast connection.
 */
public class FileSender {


    /**
     * Peer associated with the sender.
     */
    protected Peer peer;

    /**
     * Path to the file.
     */
    private String pathname;

    /**
     * Replication degree of the file.
     */
    protected Integer replicationDegree;

    /**
     * Saves all handlers for the messages sent.
     */
    protected ArrayList<StoredInitiatorHandler> handlers;

    /**
     * Latch that waits fot all handlers to conclude.
     */
    protected CountDownLatch chunkSavedSignal;

    /**
     * File to send.
     */
    protected File file;
    protected String encodedFileName;


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
        encodedFileName = SHA256Encoder.encode(this.peer.getPeerId() + "/" + file.getName());
    }

    /**
     * Initiates the sending and subscription of a PUTCHUNK message.
     * @param replicationDegree Replication degree of the message.
     * @param encodedFileName Encoded name of the file.
     * @param body Body of the message.
     * @param chunkNo Chunk number of the message.
     * @return Handler for the message subscription and retransmission.
     */
    protected StoredInitiatorHandler sendChunk(Integer replicationDegree, String encodedFileName, byte[] body, int chunkNo) {
        this.peer.getFileManager().addRemoteChunk(encodedFileName, chunkNo, replicationDegree, body.length);
        PutChunkMessage msg = new PutChunkMessage(peer.getPeerId(), encodedFileName, chunkNo, replicationDegree, body);
        StoredInitiatorHandler handler = new StoredInitiatorHandler(this.peer, msg, this.chunkSavedSignal);
        handler.startAsync();
        this.peer.getControl().subscribe(handler);
        return handler;
    }

    /**
     * Initiates the transfer of file.
     * @return True if the transfer was started successfully.
     */
    boolean sendFile() {

        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[MulticastConnection.CHUNK_SIZE];
            double nChunks = data.length() / (double) MulticastConnection.CHUNK_SIZE;

            this.chunkSavedSignal = new CountDownLatch((int) (Math.ceil(nChunks) + ((nChunks == Math.floor(nChunks)) ? 1 : 0)));

            int i;
            for (i = 0; i < nChunks; i++) {
                int dataLength = data.read(buffer);
                if (dataLength == -1)
                    throw  new Exception("Error reading file");
                byte[] body = Arrays.copyOfRange(buffer, 0, dataLength);
                this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, body, i));
            }

            if (nChunks == Math.floor(nChunks)) {
                this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, new byte[0], i));
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Waits for the file transfer to end.
     * @return True if the transfer was successful.
     */
    boolean waitOperation() {
        if (chunkSavedSignal == null || this.handlers.size() == 0)
            return true;

        try {
            this.chunkSavedSignal.await();
        } catch (InterruptedException e) {
            return false;
        }


        for (StoredInitiatorHandler handler : this.handlers) {
            if (!handler.wasSuccessful()) {
                NetworkLogger.printLog(Level.SEVERE, "Failed to get chunk no." + ((ChunkSubscription) handler.getSub()).getChunkNo());
                return false;
            }
        }
        return true;
    }

    public String getEncodedFileName() {
        return encodedFileName;
    }

    public String getFileName() {
        return this.file.getName();
    }
}