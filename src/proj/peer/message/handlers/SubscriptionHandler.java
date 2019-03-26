package proj.peer.message.handlers;

import proj.peer.message.subscriptions.OperationSubscription;

public abstract class SubscriptionHandler implements SubscriptionHandlerInterface {
    protected OperationSubscription sub;

    public OperationSubscription getSub() {
        return this.sub;
    }

}
