package proj.peer.message.handlers.async;

import proj.peer.Peer;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.ChunkSubscription;

import java.util.concurrent.CountDownLatch;

public class ChunkMsgHandler extends RetransmissionHandler {
    private byte[] body;

    public ChunkMsgHandler(Peer peer, GetChunkMessage msg, CountDownLatch countDownLatch) {
        super(peer.getScheduler(), peer.getControl(), peer.getRestore(), msg, countDownLatch);
        this.sub = new ChunkSubscription(ChunkMessage.OPERATION, msg.getFileId(), msg.getChunkNo());
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessage) {
            this.cancel();
            this.body = ((ChunkMessage) msg).getBody();
            this.subscriptionConnection.unsubscribe(this.sub);
            this.successful = true;
            this.countDown();
        }

    }

    @Override
    public boolean wasSuccessful() {
        return this.successful;
    }

    public byte[] getBody() {
        return this.body;
    }
}
