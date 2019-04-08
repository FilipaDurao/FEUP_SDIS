package proj.peer.subscriptions;

import proj.peer.handlers.SubscriptionHandlerInterface;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;
import proj.peer.subscriptions.ChunkSubscription;
import proj.peer.subscriptions.FileSubscription;
import proj.peer.subscriptions.OperationSubscription;

import java.util.concurrent.ConcurrentHashMap;

public class SubscriptionManager {
    protected ConcurrentHashMap<OperationSubscription, SubscriptionHandlerInterface> subscriptions;

    public SubscriptionManager() {
        this.subscriptions =  new ConcurrentHashMap<>();
    }

    public void subscribe(SubscriptionHandlerInterface handler) {
        this.subscriptions.put(handler.getSub(), handler);
    }

    public void unsubscribe(OperationSubscription sub) {
        if (subscriptions.containsKey(sub))
            this.subscriptions.remove(sub);
    }

    public boolean checkForSubscription(Message msg) {
        if (msg instanceof MessageChunk) {
            MessageChunk msgC = (MessageChunk) msg;
            OperationSubscription possibleSub = new ChunkSubscription(msgC.getOperation(), msgC.getFileId(), msgC.getChunkNo(), msgC.getVersion());
            if (subscriptions.containsKey(possibleSub)) {
                subscriptions.get(possibleSub).notify(msg);
                return true;
            }
        }

        OperationSubscription possibleFileSub = new FileSubscription(msg.getOperation(), msg.getFileId(), msg.getVersion());
        OperationSubscription possibleOpSub = new OperationSubscription(msg.getOperation(), msg.getVersion());

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