package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.operations.PutChunkTCPOperation;
import proj.peer.utils.RandomGenerator;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PutChunkTCPHandler extends SubscriptionHandler {
    public PutChunkTCPHandler(SubscriptionConnection subscriptionConnection, Peer peer) {
        super(new OperationSubscription(PutChunkMessage.OPERATION, peer.getVersion()), subscriptionConnection, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof PutChunkMessage) {
            NetworkLogger.printLog(Level.INFO, "Received TCP PUTCHUNK message - " + msg.getTruncatedFilename() + " - " + ((PutChunkMessage) msg).getChunkNo());
            if (!this.peer.getFileManager().isFileRemotlyStored(msg.getFileId()) && this.peer.getFileManager().hasSpace()) {
                int delay = RandomGenerator.getNumberInRange(0, 400);
                this.peer.getScheduler().schedule(new PutChunkTCPOperation((PutChunkMessage) msg, peer), delay, TimeUnit.MILLISECONDS);

            }
        }
    }
}
