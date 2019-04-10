package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;
import proj.peer.operations.GetChunkTCPOperation;
import proj.peer.operations.LatchedUnsubscribeOperation;
import proj.peer.utils.RandomGenerator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GetChunkTCPHandler extends SubscriptionHandler {

    public GetChunkTCPHandler(SubscriptionConnection subscriptionConnection, Peer peer) {
        super(new OperationSubscription(GetChunkMessage.OPERATION, peer.getVersion()), subscriptionConnection, peer);

    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            GetChunkMessage chunkMessage = (GetChunkMessage) msg;
            if (this.peer.getFileManager().isChunkSaved(chunkMessage.getFileId(), chunkMessage.getChunkNo())) {
                NetworkLogger.printLog(Level.INFO, "Message received - " + msg.getOperation() + " " + msg.getTruncatedFilename() + " " + ((MessageChunk) msg).getChunkNo(), this.subscriptionConnection.getConnectionName());

                int delay = RandomGenerator.getNumberInRange(0, 400);
                CountDownLatch latch = new CountDownLatch(1);
                GetChunkTCPOperation getChunkTCPOperation = new GetChunkTCPOperation(chunkMessage, peer, latch);
                Future future = this.peer.getScheduler().schedule(getChunkTCPOperation, delay, TimeUnit.MILLISECONDS);
                ChunkTCPSenderHandler handler = new ChunkTCPSenderHandler(chunkMessage.getFileId(), chunkMessage.getChunkNo(), future, latch, getChunkTCPOperation, peer.getRestore(), peer);
                this.peer.getRestore().subscribe(handler);
                this.peer.getScheduler().submit(new LatchedUnsubscribeOperation(handler, latch));
            }
        }

    }
}
