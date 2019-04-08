package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnectionInterface;
import proj.peer.subscriptions.OperationSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.improvements.GetChunkTCPMessage;
import proj.peer.operations.GetChunkTCPOperation;

import java.util.logging.Level;

public class GetChunkTCPHandler extends SubscriptionHandler {
    public GetChunkTCPHandler(Peer peer, SubscriptionConnectionInterface subscriptionConnection) {
        super(new OperationSubscription(GetChunkTCPMessage.OPERATION, peer.getVersion()), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkTCPMessage) {
            NetworkLogger.printLog(Level.INFO, "TCP request received");
            this.peer.getScheduler().execute(new GetChunkTCPOperation((GetChunkTCPMessage) msg, peer));
        }
    }
}
