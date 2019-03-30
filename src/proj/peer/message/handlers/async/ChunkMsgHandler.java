package proj.peer.message.handlers.async;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.ChunkSubscription;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class ChunkMsgHandler extends RetransmissionHandler {
    private byte[] body;

    public ChunkMsgHandler(Peer peer, GetChunkMessage msg, CountDownLatch countDownLatch) {
        super(new ChunkSubscription(ChunkMessage.OPERATION, msg.getFileId(), msg.getChunkNo(), msg.getVersion()), peer, peer.getControl(), peer.getRestore(), msg, countDownLatch);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessage) {
            ChunkMessage chunkMessage = (ChunkMessage) msg;
            NetworkLogger.printLog(Level.INFO, "Received requested chunk no." + chunkMessage.getChunkNo());
            this.cancel();
            this.body = chunkMessage.getBody();
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
