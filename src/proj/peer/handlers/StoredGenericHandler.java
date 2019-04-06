package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.StoredMessage;
import proj.peer.handlers.subscriptions.OperationSubscription;

public class StoredGenericHandler extends SubscriptionHandler {


    public StoredGenericHandler(Peer peer, SubscriptionConnection subscriptionConnection) {
        super(new OperationSubscription(StoredMessage.OPERATION, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof StoredMessage) {
            StoredMessage storedMessage = (StoredMessage) msg;
            peer.getFileManager().storeChunkPeer(msg.getFileId(), storedMessage.getChunkNo(), storedMessage.getSenderId());
        }
    }

    @Override
    public OperationSubscription getSub() {
        return this.sub;
    }
}
