package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.operations.PutChunkOperation;

import java.util.logging.Level;

public class PutChunkHandler extends SubscriptionHandler {


    public PutChunkHandler(Peer peer, SubscriptionConnection subscriptionConnection) {
        super(new OperationSubscription(PutChunkMessage.OPERATION, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof PutChunkMessage) {
            NetworkLogger.printLog(Level.INFO, "Received PUTCHUNK message - " + msg.getTruncatedFilename() + " - " + ((PutChunkMessage) msg).getChunkNo());
            if (!this.peer.getFileManager().isFileRemotlyStored(msg.getFileId()))
                this.peer.getScheduler().execute(new PutChunkOperation((PutChunkMessage) msg, peer));
        }
    }
}
