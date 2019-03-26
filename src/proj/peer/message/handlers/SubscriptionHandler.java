package proj.peer.message.handlers;

import proj.peer.message.Message;
import proj.peer.message.subscriptions.OperationSubscription;


public interface SubscriptionHandler {

    void notify(Message msg);

    OperationSubscription getSub();

}
