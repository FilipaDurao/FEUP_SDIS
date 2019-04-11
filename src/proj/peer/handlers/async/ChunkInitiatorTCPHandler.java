package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.handlers.BodyReceiver;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessageTCP;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;
import proj.peer.operations.GetTCPMessageOperation;
import proj.peer.operations.SaveFileOperation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class ChunkInitiatorTCPHandler extends ChunkInitiatorHandler implements BodyReceiver {
    private Future future;

    public ChunkInitiatorTCPHandler(Peer peer, GetChunkMessage msg, SaveFileOperation chunkSaver, CountDownLatch countDownLatch) {
        super(peer, msg, chunkSaver, countDownLatch);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof ChunkMessageTCP) {
            ChunkMessageTCP chunkMessage = (ChunkMessageTCP) msg;
            NetworkLogger.printLog(Level.INFO, "Received TCP chunk - " + chunkMessage.getTruncatedFilename() + " - " + chunkMessage.getChunkNo() + " - " + chunkMessage.getHostname() + " - " + chunkMessage.getPort());
            future =  this.peer.getScheduler().submit(new GetTCPMessageOperation(chunkMessage.getHostname(), chunkMessage.getPort(), this));
        }
    }

    @Override
    public synchronized void shutdown() {
        if(this.future != null)
            this.future.cancel(true);
        super.shutdown();
    }

    @Override
    public void setBody(byte[] body) {
        NetworkLogger.printLog(Level.INFO, "Received requested chunk no." + ((MessageChunk) msg).getChunkNo());
        this.body = body;
        this.chunkSaver.addChunk(((MessageChunk) this.msg).getChunkNo(), body);
        this.successful = true;
        this.shutdown();
    }
}
