package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.handlers.SubscriptionHandlerInterface;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;
import proj.peer.message.subscriptions.ChunkSubscription;
import proj.peer.message.subscriptions.FileSubscription;
import proj.peer.message.subscriptions.OperationSubscription;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SubscriptionConnection extends RunnableMC {
    protected Peer peer;
    protected ConcurrentHashMap<OperationSubscription, SubscriptionHandlerInterface> subscriptions;

    public SubscriptionConnection(String multicast_name, Integer multicast_port_number, Peer peer) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
        this.subscriptions = new ConcurrentHashMap<>();
    }

    public void subscribe(SubscriptionHandlerInterface handler) {
        this.subscriptions.put(handler.getSub(), handler);
    }

    public void unsubscribe(OperationSubscription sub) {
        if (subscriptions.containsKey(sub))
            this.subscriptions.remove(sub);
    }

    protected boolean checkForSubscription(Message msg) {
        if (msg instanceof MessageChunk) {
            MessageChunk msgC = (MessageChunk) msg;
            OperationSubscription possibleSub = new ChunkSubscription(msgC.getOperation(), msgC.getFileId(), msgC.getChunkNo());
            if (subscriptions.containsKey(possibleSub)) {
                subscriptions.get(possibleSub).notify(msg);
                return true;
            }
        }

        OperationSubscription possibleFileSub = new FileSubscription(msg.getOperation(), msg.getFileId());
        OperationSubscription possibleOpSub = new OperationSubscription(msg.getOperation());

        if (subscriptions.containsKey(possibleFileSub)) {
            subscriptions.get(possibleFileSub).notify(msg);
            return true;
        } else if (subscriptions.containsKey(possibleOpSub)) {
            subscriptions.get(possibleOpSub).notify(msg);
            return true;
        }

        return false;
    }
}
