package proj.peer.handlers;

import proj.peer.message.messages.Message;
import proj.peer.handlers.subscriptions.OperationSubscription;


public interface SubscriptionHandlerInterface {

    void notify(Message msg);

    OperationSubscription getSub();

}
