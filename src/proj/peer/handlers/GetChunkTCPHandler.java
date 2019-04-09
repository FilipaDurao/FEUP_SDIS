package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.operations.GetChunkTCPOperation;
import proj.peer.operations.LatchedUnsubscribeOperation;
import proj.peer.utils.RandomGenerator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GetChunkTCPHandler extends SubscriptionHandler {

    public GetChunkTCPHandler(SubscriptionConnection subscriptionConnection, Peer peer) {
        super(new OperationSubscription(GetChunkMessage.OPERATION, peer.getVersion()), subscriptionConnection, peer);

    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            GetChunkMessage chunkMessage = (GetChunkMessage) msg;
            if (this.peer.getFileManager().isChunkSaved(chunkMessage.getFileId(), chunkMessage.getChunkNo())) {
                int delay = RandomGenerator.getNumberInRange(0, 400);
                CountDownLatch latch = new CountDownLatch(1);
                Future future = this.peer.getScheduler().schedule(new GetChunkTCPOperation(chunkMessage, peer, latch), delay, TimeUnit.MILLISECONDS);
                ChunkTCPSenderHandler handler = new ChunkTCPSenderHandler(chunkMessage.getFileId(), chunkMessage.getChunkNo(), future, peer.getRestore(), peer);
                this.peer.getRestore().subscribe(handler);
                this.peer.getScheduler().submit(new LatchedUnsubscribeOperation(handler, latch));
            }
        }

    }
}
