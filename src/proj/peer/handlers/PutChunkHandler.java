package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnectionInterface;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.subscriptions.OperationSubscription;
import proj.peer.operations.PutChunkOperation;

import java.util.logging.Level;

public class PutChunkHandler extends SubscriptionHandler {


    public PutChunkHandler(Peer peer, SubscriptionConnectionInterface subscriptionConnection) {
        super(new OperationSubscription(PutChunkMessage.OPERATION, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof PutChunkMessage) {
            NetworkLogger.printLog(Level.INFO, "Received PUTCHUNK message - " + msg.getTruncatedFilename() + " - " + ((PutChunkMessage) msg).getChunkNo());
            this.peer.getScheduler().execute(new PutChunkOperation((PutChunkMessage) msg, peer));
        }
    }
}
