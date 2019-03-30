package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.message.subscriptions.OperationSubscription;

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
