package proj.peer.message.handlers.async;

import proj.peer.message.handlers.SubscriptionHandler;
import proj.peer.message.handlers.SubscriptionHandlerInterface;

import java.util.concurrent.CountDownLatch;

abstract class AsyncHandler extends SubscriptionHandler implements AsyncHandlerInterface {
    private CountDownLatch countDownLatch;

    AsyncHandler(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    protected void countDown() {
        if (this.countDownLatch != null)
            this.countDownLatch.countDown();
    }

}
