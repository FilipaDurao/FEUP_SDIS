package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.Message;

import java.util.concurrent.Future;
import java.util.logging.Level;

public class ChunkSenderHandler extends SubscriptionHandler {
    private Future sendChunk;

    public ChunkSenderHandler(String fileId, Integer chunkNo, Future sendChunk, SubscriptionConnection subscriptionConnection, Peer peer) {
        super(new ChunkSubscription(ChunkMessage.OPERATION, fileId, chunkNo, Peer.DEFAULT_VERSION), subscriptionConnection, peer);
        this.sendChunk = sendChunk;
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessage) {
            if (!this.sendChunk.isDone()) {
                NetworkLogger.printLog(Level.INFO, "Received chunk scheduled to be sent " + msg.getTruncatedFilename() + " " + ((ChunkMessage) msg).getChunkNo());
                this.sendChunk.cancel(true);
                this.unsubscribe();
            }
        }
    }
}
