package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.OperationSubscription;
import proj.peer.operations.GetChunkOperation;


public class GetChunkHandler extends SubscriptionHandler {
    private Peer peer;

    public GetChunkHandler(Peer peer) {
        this.peer = peer;
        this.sub = new OperationSubscription(GetChunkMessage.OPERATION);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            System.out.println(String.format("Get Chunk Message Received: %s %s %s", msg.getOperation(), msg.getSenderId(), msg.getFileId()));
            this.peer.getScheduler().execute(new GetChunkOperation((GetChunkMessage) msg, peer));
        }
    }
}
