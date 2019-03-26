package proj.peer.message.handlers;

import proj.peer.message.Message;
import proj.peer.message.subscriptions.FileSubscription;


public interface SubscriptionHandler {

    void notify(Message msg);

    FileSubscription getSub();

    boolean wasSuccessful();
}
