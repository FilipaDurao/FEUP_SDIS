package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.operations.SaveFileOperation;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class ChunkInitiatorHandler extends AsyncHandler {
    protected byte[] body;
    protected SaveFileOperation chunkSaver;

    public ChunkInitiatorHandler(Peer peer, GetChunkMessage msg, SaveFileOperation chunkSaver, CountDownLatch countDownLatch) {
        super(new ChunkSubscription(ChunkMessage.OPERATION, msg.getFileId(), msg.getChunkNo(), msg.getVersion()), peer.getRestore(), peer.getControl(), msg, countDownLatch, peer);
        this.chunkSaver = chunkSaver;
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessage) {
            ChunkMessage chunkMessage = (ChunkMessage) msg;
            NetworkLogger.printLog(Level.INFO, "Received requested chunk no." + chunkMessage.getChunkNo());
            this.chunkSaver.addChunk(chunkMessage.getChunkNo(), chunkMessage.getBody());
            this.successful = true;
            this.shutdown();
        }
    }

}
