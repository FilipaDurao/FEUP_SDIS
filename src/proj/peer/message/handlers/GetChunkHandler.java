package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.message.MessageSender;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.OperationSubscription;
import proj.peer.utils.RandomGenerator;

import java.util.concurrent.TimeUnit;

public class GetChunkHandler extends SubscriptionHandler {
    private Peer peer;

    public GetChunkHandler(Peer peer) {
        this.peer = peer;
        this.sub = new OperationSubscription(GetChunkMessage.OPERATION);
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof GetChunkMessage) {
            try {
                System.out.println(String.format("Get Chunk Message Received: %s %s %s", msg.getOperation(), msg.getSenderId(), msg.getFileId()));
                GetChunkMessage getChunkMessage = (GetChunkMessage) msg;
                byte[] body = this.peer.getFileManager().getChunk(getChunkMessage.getFileId(), getChunkMessage.getChunkNo());
                ChunkMessage response = new ChunkMessage(peer.getVersion(), peer.getPeerId(), getChunkMessage.getFileId(), getChunkMessage.getChunkNo(), body);
                int delay = RandomGenerator.getNumberInRange(0, 400);
                this.peer.getScheduler().schedule(new MessageSender(peer.getRestore(), response), delay, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failure sending chunk.");
            }


        }

    }
}
