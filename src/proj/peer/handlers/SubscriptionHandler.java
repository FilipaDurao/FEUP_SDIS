package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.subscriptions.OperationSubscription;

public abstract class SubscriptionHandler implements SubscriptionHandlerInterface {
    protected OperationSubscription sub;
    protected SubscriptionConnection subscriptionConnection;
    protected Peer peer;

    public SubscriptionHandler(OperationSubscription sub, SubscriptionConnection subscriptionConnection, Peer peer) {
        this.sub = sub;
        this.subscriptionConnection = subscriptionConnection;
        this.peer = peer;
    }

    public OperationSubscription getSub() {
        return this.sub;
    }

    public SubscriptionConnection getSubscriptionConnection() {
        return subscriptionConnection;
    }

    public void unsubscribe() {
        this.subscriptionConnection.unsubscribe(this.sub);
    }
}
