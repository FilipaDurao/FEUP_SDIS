package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.subscriptions.ChunkSubscription;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public class StoredInitiatorHandler extends AsyncHandler {

    private HashSet<String> storedIds;

    public StoredInitiatorHandler(Peer peer, PutChunkMessage msg, CountDownLatch chunkSavedSignal) {
        super(new ChunkSubscription(StoredMessage.OPERATION, msg.getFileId(), msg.getChunkNo(), msg.getVersion()), peer.getControl(), peer.getBackup(), msg, chunkSavedSignal, peer);
        this.storedIds = new HashSet<>();
    }

    public void addStoredId(String storedId) {
        this.storedIds.add(storedId);
    }


    @Override
    public void notify(Message response) {
        if (response instanceof StoredMessage) {
            if (!storedIds.contains(response.getSenderId())) {
                this.addStoredId(response.getSenderId());
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
