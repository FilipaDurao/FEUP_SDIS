package proj.peer.message.handlers.async;

import proj.peer.Peer;
import proj.peer.message.handlers.SubscriptionHandler;
import proj.peer.message.subscriptions.OperationSubscription;

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
