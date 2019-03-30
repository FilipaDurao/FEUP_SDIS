package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.OperationSubscription;
import proj.peer.operations.GetChunkOperation;

import java.util.logging.Level;


public class GetChunkHandler extends SubscriptionHandler {

    public GetChunkHandler(Peer peer, String version) {
        super(new OperationSubscription(GetChunkMessage.OPERATION, version), peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            NetworkLogger.printLog(Level.INFO, "Chunk request received");
            this.peer.getScheduler().execute(new GetChunkOperation((GetChunkMessage) msg, peer));
        }
    }
}
