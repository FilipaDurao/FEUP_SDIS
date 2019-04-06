package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.handlers.subscriptions.ChunkSubscription;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public class StoredInitiatorHandler extends RetransmissionHandler {

    private HashSet<String> storedIds;

    public StoredInitiatorHandler(Peer peer, PutChunkMessage msg, CountDownLatch chunkSavedSignal) {
        super(new ChunkSubscription(StoredMessage.OPERATION, msg.getFileId(), msg.getChunkNo(), msg.getVersion()), peer, peer.getBackup(), peer.getControl(), msg, chunkSavedSignal);
        this.storedIds = new HashSet<>();
    }


    @Override
    public void notify(Message response) {
        if (response instanceof StoredMessage) {
            if (!storedIds.contains(response.getSenderId())) {
                this.storedIds.add(response.getSenderId());
                if (this.storedIds.size() >= ((PutChunkMessage) this.msg).getReplicationDegree()) {
                    this.cancel();
                    this.unsubscribe();
                    this.successful = true;
                    this.countDown();
                }
            }
        }
    }


    @Override
    public boolean wasSuccessful() {
        return this.successful;
    }
}
