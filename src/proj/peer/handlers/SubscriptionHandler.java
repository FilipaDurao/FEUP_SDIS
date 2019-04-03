package proj.peer.handlers;

import proj.peer.Peer;
import proj.peer.handlers.subscriptions.OperationSubscription;

public abstract class SubscriptionHandler implements SubscriptionHandlerInterface {
    protected OperationSubscription sub;
    protected Peer peer;

    public SubscriptionHandler(OperationSubscription sub, Peer peer) {
        this.sub = sub;
        this.peer = peer;
    }

    public OperationSubscription getSub() {
        return this.sub;
    }

}
