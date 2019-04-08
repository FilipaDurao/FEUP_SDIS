package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnectionInterface;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.subscriptions.OperationSubscription;
import proj.peer.operations.GetChunkOperation;

import java.util.logging.Level;


public class GetChunkHandler extends SubscriptionHandler {

    public GetChunkHandler(Peer peer, SubscriptionConnectionInterface subscriptionConnection) {
        super(new OperationSubscription(GetChunkMessage.OPERATION, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            NetworkLogger.printLog(Level.INFO, "Chunk request received " + msg.getTruncatedFilename() + " " + ((GetChunkMessage) msg).getChunkNo());
            this.peer.getScheduler().execute(new GetChunkOperation((GetChunkMessage) msg, peer));
        }
    }
}
