package proj.peer.message.handlers;

import proj.peer.message.messages.Message;
import proj.peer.message.subscriptions.OperationSubscription;


public interface SubscriptionHandlerInterface {

    void notify(Message msg);

    OperationSubscription getSub();

}
