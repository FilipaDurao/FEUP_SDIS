package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.subscriptions.ChunkSubscription;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class ChunkInitiatorHandler extends AsyncHandler {
    private byte[] body;

    public ChunkInitiatorHandler(Peer peer, GetChunkMessage msg, CountDownLatch countDownLatch) {
        super(new ChunkSubscription(ChunkMessage.OPERATION, msg.getFileId(), msg.getChunkNo(), msg.getVersion()), peer.getRestore(), peer.getControl(), msg, countDownLatch, peer);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessage) {
            ChunkMessage chunkMessage = (ChunkMessage) msg;
            NetworkLogger.printLog(Level.INFO, "Received requested chunk no." + chunkMessage.getChunkNo());
            this.cancel();
            this.body = chunkMessage.getBody();
            this.unsubscribe();
            this.successful = true;
            this.countDown();
        }

    }

    public byte[] getBody() {
        return this.body;
    }
}
