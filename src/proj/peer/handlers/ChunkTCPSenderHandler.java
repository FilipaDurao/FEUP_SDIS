package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.ChunkMessageTCP;
import proj.peer.message.messages.Message;
import proj.peer.operations.GetChunkTCPOperation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class ChunkTCPSenderHandler extends SubscriptionHandler {
    private Future sendChunk;
    private CountDownLatch latch;
    private GetChunkTCPOperation tcpOperation;

    public ChunkTCPSenderHandler(String fileId, Integer chunkNo, Future sendChunk, CountDownLatch latch, GetChunkTCPOperation tcpOperation, SubscriptionConnection subscriptionConnection, Peer peer) {
        super(new ChunkSubscription(ChunkMessageTCP.OPERATION, fileId, chunkNo, peer.getVersion()), subscriptionConnection, peer);
        this.sendChunk = sendChunk;
        this.latch = latch;
        this.tcpOperation = tcpOperation;
    }

    @Override
    public void notify(Message msg) {
        try {
            if (msg instanceof ChunkMessageTCP) {
                if (!this.sendChunk.isDone()) {
                    NetworkLogger.printLog(Level.INFO, "Received TCP chunk scheduled to be sent " + msg.getTruncatedFilename() + " " + ((ChunkMessage) msg).getChunkNo());
                    this.sendChunk.cancel(true);
                    this.tcpOperation.closeSockets();
                    this.unsubscribe();
                    this.latch.countDown();
                }
            }
        } catch (Exception e ) {
            NetworkLogger.printLog(Level.WARNING, "Failure in send TCP chunk cancellation - " + e.getMessage());
        }
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }
}