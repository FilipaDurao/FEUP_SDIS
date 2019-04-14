package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.handlers.async.StoredInitiatorHandler;
import proj.peer.handlers.async.StoredInitiatorTCPHandler;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.PutChunkMessageTCP;

import java.util.concurrent.CountDownLatch;

public class FileSenderTCP extends FileSender {
    /**
     * Sends a file via a tcp connection.
     *
     * @param peer              Peer associated with the sender.
     * @param pathname          Path to the file.
     * @param replicationDegree Replication degree of the file.
     */
    FileSenderTCP(Peer peer, String pathname, Integer replicationDegree) {
        super(peer, pathname, replicationDegree);
    }

    @Override
    protected StoredInitiatorHandler sendChunk(Integer replicationDegree, String encodedFileName, byte[] body, int chunkNo, CountDownLatch latch, int size) {
        this.peer.getFileManager().addRemoteChunk(encodedFileName, chunkNo, replicationDegree, 0);
        PutChunkMessage msg = new PutChunkMessageTCP(peer.getVersion(), peer.getPeerId(), encodedFileName, chunkNo, replicationDegree, size);
        StoredInitiatorHandler handler = new StoredInitiatorTCPHandler(this.peer, msg, latch, this.file.getAbsolutePath());
        handler.startAsync();
        this.peer.getControl().subscribe(handler);
        return handler;
    }

    @Override
    boolean sendFile() {
        try {
            double nParts = this.file.length() / (double) MulticastConnection.CHUNK_SIZE;
            int nChunks = (int) (Math.ceil(nParts) + ((nParts == Math.floor(nParts)) ? 1 : 0));
            CountDownLatch latch = null;
            int i;
            for (i = 0; i < nChunks; i++) {
                if (i % WINDOW_SIZE == 0) {

                    this.awaitLatch(latch);
                    latch = new CountDownLatch(Math.min(WINDOW_SIZE, nChunks - i));
                }
                this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, null, i, latch, i == nChunks - 1 ? (int) (this.file.length() % 64000) : 64000));
            }

            this.awaitLatch(latch);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
