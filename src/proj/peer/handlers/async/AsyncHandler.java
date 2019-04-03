package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.handlers.SubscriptionHandler;
import proj.peer.handlers.subscriptions.OperationSubscription;

import java.util.concurrent.CountDownLatch;

abstract class AsyncHandler extends SubscriptionHandler implements AsyncHandlerInterface {
    private CountDownLatch countDownLatch;

    AsyncHandler(OperationSubscription sub, Peer peer, CountDownLatch countDownLatch) {
        super(sub, peer);
        this.countDownLatch = countDownLatch;
    }

    protected void countDown() {
        if (this.countDownLatch != null)
            this.countDownLatch.countDown();
    }

}
