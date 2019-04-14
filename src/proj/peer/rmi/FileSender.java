package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.handlers.async.StoredInitiatorHandler;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.PutChunkMessage;
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


    protected static final int WINDOW_SIZE = 7;
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
     * File to send.
     */
    protected File file;
    protected String encodedFileName;


    /**
     * Sends a file via a multicast connection.
     *
     * @param peer              Peer associated with the sender.
     * @param pathname          Path to the file.
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
     *
     * @param replicationDegree Replication degree of the message.
     * @param encodedFileName   Encoded name of the file.
     * @param body              Body of the message.
     * @param chunkNo           Chunk number of the message.
     * @return Handler for the message subscription and retransmission.
     */
    protected StoredInitiatorHandler sendChunk(Integer replicationDegree, String encodedFileName, byte[] body, int chunkNo, CountDownLatch latch) {
        this.peer.getFileManager().addRemoteChunk(encodedFileName, chunkNo, replicationDegree, 0);
        PutChunkMessage msg = new PutChunkMessage(peer.getPeerId(), encodedFileName, chunkNo, replicationDegree, body);
        StoredInitiatorHandler handler = new StoredInitiatorHandler(this.peer, msg, latch);
        handler.startAsync();
        this.peer.getControl().subscribe(handler);
        return handler;
    }

    /**
     * Initiates the transfer of file.
     *
     * @return True if the transfer was started successfully.
     */
    boolean sendFile() {

        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[MulticastConnection.CHUNK_SIZE];
            double numberOfParts = data.length() / (double) MulticastConnection.CHUNK_SIZE;

            int nChunks = (int) (Math.ceil(numberOfParts) + ((numberOfParts == Math.floor(numberOfParts)) ? 1 : 0));
            CountDownLatch latch = null;
            int i;
            for (i = 0; i < nChunks; i++) {
                if (i % WINDOW_SIZE == 0) {

                    awaitLatch(latch);
                    latch = new CountDownLatch(Math.min(WINDOW_SIZE, nChunks - i));
                }
                int dataLength = data.read(buffer);
                if (dataLength == -1) {
                    if (i == nChunks - 1) {
                        this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, new byte[0], i, latch));
                    } else {
                        throw new Exception("Error reading file");
                    }
                } else {
                    byte[] body = Arrays.copyOfRange(buffer, 0, dataLength);
                    this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, body, i, latch));
                }
            }

            awaitLatch(latch);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    protected void awaitLatch(CountDownLatch latch) throws Exception {
        if (latch != null) {
            latch.await();

            for (StoredInitiatorHandler handler : handlers) {
                if (!handler.wasSuccessful())
                    throw new Exception("Get chunk not successful");
            }
        }
    }

    public String getEncodedFileName() {
        return encodedFileName;
    }

    public String getFileName() {
        return this.file.getName();
    }
}