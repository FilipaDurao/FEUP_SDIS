package proj.peer.message.handlers.async;

import proj.peer.Peer;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.ChunkSubscription;

import java.util.concurrent.CountDownLatch;

public class ChunkHandler extends RetransmissionHandler {
    public ChunkHandler(Peer peer, GetChunkMessage msg, CountDownLatch countDownLatch) {
        super(peer.getScheduler(), peer.getControl(), peer.getRestore(), msg, countDownLatch);
        this.sub = new ChunkSubscription(ChunkMessage.OPERATION, msg.getFileId(), msg.getChunkNo());
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessage) {
            this.cancel();
            this.subscriptionConnection.unsubscribe(this.sub);
            this.successful = true;
            this.countDown();
        }

    }

    @Override
    public boolean wasSuccessful() {
        return false;
    }
}
