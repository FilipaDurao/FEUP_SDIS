package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.subscriptions.OperationSubscription;
import proj.peer.operations.PutChunkOperation;

public class PutChunkHandler extends SubscriptionHandler {


    public PutChunkHandler(Peer peer) {
        super(new OperationSubscription(PutChunkMessage.OPERATION, Peer.DEFAULT_VERSION), peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof PutChunkMessage) {
            this.peer.getScheduler().execute(new PutChunkOperation((PutChunkMessage) msg, peer));
        }
    }
}
