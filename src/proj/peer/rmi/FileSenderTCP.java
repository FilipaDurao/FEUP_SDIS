package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.handlers.async.StoredInitiatorHandler;
import proj.peer.handlers.async.StoredInitiatorTCPHandler;
import proj.peer.message.messages.PutChunkMessage;

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
    protected StoredInitiatorHandler sendChunk(Integer replicationDegree, String encodedFileName, byte[] body, int chunkNo) {
        this.peer.getFileManager().addRemoteChunk(encodedFileName, chunkNo, replicationDegree, 0);
        PutChunkMessage msg = new PutChunkMessage(peer.getVersion(), peer.getPeerId(), encodedFileName, chunkNo, replicationDegree);
        StoredInitiatorHandler handler = new StoredInitiatorTCPHandler(this.peer, msg, this.chunkSavedSignal, this.file.getAbsolutePath());
        handler.startAsync();
        this.peer.getControl().subscribe(handler);
        return handler;
    }

    @Override
    boolean sendFile() {
        try {
            double nChunks = this.file.length() / (double) MulticastConnection.CHUNK_SIZE;
            this.chunkSavedSignal = new CountDownLatch((int) (Math.ceil(nChunks) + ((nChunks == Math.floor(nChunks)) ? 1 : 0)));

            int i;
            for (i = 0; i < nChunks; i++) {
                this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, null, i));
            }

            if (nChunks == Math.floor(nChunks)) {
                this.handlers.add(this.sendChunk(replicationDegree, encodedFileName, new byte[0], i));
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
