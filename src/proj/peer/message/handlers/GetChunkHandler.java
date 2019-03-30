package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.OperationSubscription;
import proj.peer.operations.GetChunkOperation;


public class GetChunkHandler extends SubscriptionHandler {

    public GetChunkHandler(Peer peer, String version) {
        super(new OperationSubscription(GetChunkMessage.OPERATION, version), peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            System.out.println(String.format("Get Chunk Message Received: %s %s", msg.getOperation(), msg.getSenderId()));
            this.peer.getScheduler().execute(new GetChunkOperation((GetChunkMessage) msg, peer));
        }
    }
}
