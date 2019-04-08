package proj.peer.connection;

import proj.peer.handlers.SubscriptionHandlerInterface;
import proj.peer.message.messages.Message;
import proj.peer.subscriptions.OperationSubscription;

public interface SubscriptionConnectionInterface {
    public void subscribe(SubscriptionHandlerInterface handler);
    public void unsubscribe(OperationSubscription sub);
    public boolean checkForSubscription(Message msg);
}
