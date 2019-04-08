package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnectionInterface;
import proj.peer.subscriptions.OperationSubscription;
import proj.peer.message.messages.DeleteMessage;
import proj.peer.message.messages.Message;
import proj.peer.operations.DeleteFileOperation;

public class DeleteHandler extends SubscriptionHandler {
    public DeleteHandler(Peer peer, SubscriptionConnectionInterface subscriptionConnection) {
        super(new OperationSubscription(DeleteMessage.OPERATION, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof DeleteMessage) {
            if (this.peer.getFileManager().isFileSaved(msg.getFileId())) {
                this.peer.getScheduler().execute(new DeleteFileOperation(peer, msg.getFileId()));
            }
        }
    }
}
