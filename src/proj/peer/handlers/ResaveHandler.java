package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.async.StoredInitiatorHandler;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;

public class ResaveHandler extends SubscriptionHandler {
    private StoredInitiatorHandler subscriptionHandler;

    public ResaveHandler(String fileId, Integer chunkNo, SubscriptionConnection subscriptionConnection, StoredInitiatorHandler subscriptionHandler, Peer peer) {
        super(new ChunkSubscription(PutChunkMessage.OPERATION, fileId, chunkNo, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
        this.subscriptionHandler = subscriptionHandler;
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof  PutChunkMessage) {
            this.subscriptionHandler.cancel();
            this.subscriptionHandler.unsubscribe();
            this.unsubscribe();
        }
    }
}
