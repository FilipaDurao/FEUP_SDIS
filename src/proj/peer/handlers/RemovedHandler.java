package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.RemovedMessage;
import proj.peer.operations.ResaveOperation;

public class RemovedHandler extends SubscriptionHandler {
    public RemovedHandler(Peer peer, SubscriptionConnection subscriptionConnection) {
        super(new OperationSubscription(RemovedMessage.OPERATION, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof RemovedMessage) {
            RemovedMessage message = (RemovedMessage) msg;
            if (this.peer.getFileManager().isChunkSaved(message.getFileId(), message.getChunkNo())) {
                this.peer.getFileManager().removeChunkPeer(message.getFileId(), message.getChunkNo(), message.getSenderId());
                this.peer.getScheduler().execute(new ResaveOperation(peer, message.getFileId(), message.getChunkNo()));
            }
        }
    }
}
