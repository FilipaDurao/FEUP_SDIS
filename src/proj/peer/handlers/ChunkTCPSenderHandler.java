package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.ChunkMessageTCP;
import proj.peer.message.messages.Message;

import java.util.concurrent.Future;
import java.util.logging.Level;

public class ChunkTCPSenderHandler extends SubscriptionHandler {
    private Future sendChunk;

    public ChunkTCPSenderHandler(String fileId, Integer chunkNo, Future sendChunk, SubscriptionConnection subscriptionConnection, Peer peer) {
        super(new ChunkSubscription(ChunkMessageTCP.OPERATION, fileId, chunkNo, peer.getVersion()), subscriptionConnection, peer);
        this.sendChunk = sendChunk;
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessageTCP) {
            if (!this.sendChunk.isDone()) {
                NetworkLogger.printLog(Level.INFO, "Received TCP chunk scheduled to be sent " + msg.getTruncatedFilename() + " " + ((ChunkMessage) msg).getChunkNo());
                this.sendChunk.cancel(true);
                this.unsubscribe();
            }
        }
    }
}