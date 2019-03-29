package proj.peer.message.handlers.async;

import proj.peer.Peer;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.message.subscriptions.ChunkSubscription;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public class StoredInitiatorHandler extends RetransmissionHandler {

    private HashSet<String> storedIds;

    public StoredInitiatorHandler(Peer peer, PutChunkMessage msg, CountDownLatch chunkSavedSignal) {
        super(peer.getScheduler(), peer.getBackup(), peer.getControl(), msg, chunkSavedSignal);
        this.storedIds = new HashSet<>();
        this.sub = new ChunkSubscription(StoredMessage.OPERATION, msg.getFileId(), msg.getChunkNo());
    }




    @Override
    public void notify(Message response) {
        if (response instanceof StoredMessage) {
            if (!storedIds.contains(response.getSenderId())) {
                this.storedIds.add(response.getSenderId());
                if (this.storedIds.size() >= ((PutChunkMessage) this.msg).getReplicationDegree()) {
                    this.cancel();
                    this.subscriptionConnection.unsubscribe(this.sub);
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
