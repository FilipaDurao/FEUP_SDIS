package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnectionInterface;
import proj.peer.subscriptions.OperationSubscription;

public abstract class SubscriptionHandler implements SubscriptionHandlerInterface {
    protected OperationSubscription sub;
    protected SubscriptionConnectionInterface subscriptionConnection;
    protected Peer peer;

    public SubscriptionHandler(OperationSubscription sub, SubscriptionConnectionInterface subscriptionConnection, Peer peer) {
        this.sub = sub;
        this.subscriptionConnection = subscriptionConnection;
        this.peer = peer;
    }

    public OperationSubscription getSub() {
        return this.sub;
    }

    public SubscriptionConnectionInterface getSubscriptionConnection() {
        return subscriptionConnection;
    }

    public void unsubscribe() {
        this.subscriptionConnection.unsubscribe(this.sub);
    }
}
