package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.operations.GetChunkTCPOperation;

public class GetChunkTCPHandler extends SubscriptionHandler {

    public GetChunkTCPHandler(SubscriptionConnection subscriptionConnection, Peer peer) {
        super(new OperationSubscription(GetChunkMessage.OPERATION, peer.getVersion()), subscriptionConnection, peer);

    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            GetChunkMessage chunkMessage = (GetChunkMessage) msg;
            if (this.peer.getFileManager().isChunkSaved(chunkMessage.getFileId(), chunkMessage.getChunkNo())) {
                this.peer.getScheduler().submit(new GetChunkTCPOperation(chunkMessage, peer));
            }
        }

    }
}
