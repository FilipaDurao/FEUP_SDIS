package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.handlers.subscriptions.ChunkSubscription;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class StoredInitiatorHandler extends AsyncHandler {

    protected HashSet<String> storedIds;

    public StoredInitiatorHandler(Peer peer, PutChunkMessage msg, CountDownLatch chunkSavedSignal) {
        super(new ChunkSubscription(StoredMessage.OPERATION, msg.getFileId(), msg.getChunkNo(), msg.getVersion()), peer.getControl(), peer.getBackup(), msg, chunkSavedSignal, peer);
        this.storedIds = new HashSet<>();
    }

    protected StoredInitiatorHandler(OperationSubscription sub, Peer peer, PutChunkMessage msg, CountDownLatch chunkSavedSignal) {
        super(sub, peer.getControl(), peer.getBackup(), msg, chunkSavedSignal, peer);
        this.storedIds = new HashSet<>();
    }

    public void addStoredId(String storedId) {
        this.storedIds.add(storedId);
    }


    @Override
    public void notify(Message response) {
        if (response instanceof StoredMessage) {
            NetworkLogger.printLog(Level.INFO, "Received Stored message - " + response.getTruncatedFilename() + " " + ((StoredMessage) response).getChunkNo());
            if (!storedIds.contains(response.getSenderId())) {
                this.addStoredId(response.getSenderId());
                this.peer.getFileManager().storeChunkPeer(response.getFileId(), ((StoredMessage) response).getChunkNo(), response.getSenderId());
                if (this.storedIds.size() >= ((PutChunkMessage) this.msg).getReplicationDegree()) {
                    this.cancel();
                    this.successful = true;
                    this.unsubscribe();
                    this.countDown();
                }
            }
        }
    }



}
