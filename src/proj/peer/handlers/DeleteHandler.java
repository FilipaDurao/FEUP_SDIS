package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.message.messages.DeleteMessage;
import proj.peer.message.messages.Message;
import proj.peer.operations.DeleteFileOperation;

public class DeleteHandler extends SubscriptionHandler {
    public DeleteHandler(Peer peer) {
        super(new OperationSubscription(DeleteMessage.OPERATION, Peer.DEFAULT_VERSION), peer);
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
