package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.handlers.SubscriptionHandlerInterface;
import proj.peer.message.messages.Message;
import proj.peer.subscriptions.OperationSubscription;
import proj.peer.subscriptions.SubscriptionManager;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public abstract class SubscriptionConnection extends MulticastConnection implements Runnable, SubscriptionConnectionInterface {
    protected SubscriptionManager subscriptionManager;
    protected Peer peer;
    private String connectionName;

    SubscriptionConnection(String connectionName, String multicast_name, Integer multicast_port_number, Peer peer) throws IOException {
        super(multicast_name, multicast_port_number);
        this.connectionName =  connectionName;
        this.peer = peer;
        this.subscriptionManager = new SubscriptionManager();
    }

    public void subscribe(SubscriptionHandlerInterface handler) {
        subscriptionManager.subscribe(handler);
    }

    public void unsubscribe(OperationSubscription sub) {
        subscriptionManager.unsubscribe(sub);
    }

    public boolean checkForSubscription(Message msg) {

        return subscriptionManager.checkForSubscription(msg);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message msg = this.getMessage();

                if (msg.getSenderId().equals(peer.getPeerId()) || subscriptionManager.checkForSubscription(msg)) {
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
