package proj.peer.operations;

import proj.peer.handlers.SubscriptionHandler;
import proj.peer.log.NetworkLogger;

import java.util.logging.Level;

public class UnsubscribeOperation implements Runnable{

    private SubscriptionHandler subscriptionHandler;

    public UnsubscribeOperation(SubscriptionHandler subscriptionHandler) {

        this.subscriptionHandler = subscriptionHandler;
    }

    @Override
    public void run() {
        NetworkLogger.printLog(Level.INFO, "Scheduled unsubscribe");
        this.subscriptionHandler.unsubscribe();
    }
}
