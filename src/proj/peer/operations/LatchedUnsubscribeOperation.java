package proj.peer.operations;

import proj.peer.handlers.SubscriptionHandler;
import proj.peer.log.NetworkLogger;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class LatchedUnsubscribeOperation extends UnsubscribeOperation {
    private CountDownLatch countDownLatch;

    public LatchedUnsubscribeOperation(SubscriptionHandler subscriptionHandler, CountDownLatch countDownLatch) {
        super(subscriptionHandler);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            this.countDownLatch.wait();
            super.run();
        } catch (InterruptedException e) {
            NetworkLogger.printLog(Level.WARNING, "Failed to unsubscribe - " + e.getMessage());
        }
    }
}
