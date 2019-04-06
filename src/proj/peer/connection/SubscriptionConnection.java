package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.handlers.SubscriptionHandlerInterface;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.handlers.subscriptions.FileSubscription;
import proj.peer.handlers.subscriptions.OperationSubscription;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public abstract class SubscriptionConnection extends MulticastConnection implements Runnable {
    protected Peer peer;
    protected ConcurrentHashMap<OperationSubscription, SubscriptionHandlerInterface> subscriptions;
    private String connectionName;

    SubscriptionConnection(String connectionName, String multicast_name, Integer multicast_port_number, Peer peer) throws IOException {
        super(multicast_name, multicast_port_number);
        this.connectionName =  connectionName;
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

    @Override
    public void run() {
        while (true) {
            try {
                Message msg = this.getMessage();

                if (msg.getSenderId().equals(peer.getPeerId()) || checkForSubscription(msg)) {
                    continue;
                }

                NetworkLogger.printLog(Level.INFO, String.format("Ignored Message - %s %s", msg.getOperation(), msg.getSenderId()), this.connectionName);
            } catch (Exception e) {
                NetworkLogger.printLog(Level.SEVERE, "Error reading incoming message - " + e.getMessage(), this.connectionName);
            }
        }
    }

    public String getConnectionName() {
        return connectionName;
    }
}
