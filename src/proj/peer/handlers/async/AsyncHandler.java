package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.SubscriptionHandler;
import proj.peer.handlers.subscriptions.OperationSubscription;

import java.util.concurrent.CountDownLatch;

abstract class AsyncHandler extends SubscriptionHandler implements AsyncHandlerInterface {
    private CountDownLatch countDownLatch;

    AsyncHandler(OperationSubscription sub, SubscriptionConnection subscriptionConnection, Peer peer, CountDownLatch countDownLatch) {
        super(sub, subscriptionConnection, peer);
        this.countDownLatch = countDownLatch;
    }

    protected void countDown() {
        if (this.countDownLatch != null)
            this.countDownLatch.countDown();
    }

}
