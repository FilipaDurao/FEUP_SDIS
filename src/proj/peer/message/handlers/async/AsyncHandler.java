package proj.peer.message.handlers.async;

import java.util.concurrent.CountDownLatch;

abstract class AsyncHandler implements AsyncHandlerInterface {
    private CountDownLatch countDownLatch;

    AsyncHandler(CountDownLatch chunkSavedSignal) {
        this.countDownLatch = chunkSavedSignal;
    }

    protected void countDown() {
        if (this.countDownLatch != null)
            this.countDownLatch.countDown();
    }
}
