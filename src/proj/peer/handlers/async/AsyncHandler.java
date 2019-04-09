package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.SubscriptionHandler;
import proj.peer.handlers.subscriptions.OperationSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.operations.RetransmitMessageOperation;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public abstract class AsyncHandler extends SubscriptionHandler implements AsyncHandlerInterface {
    private final RetransmitMessageOperation operation;
    private final SubscriptionConnection senderConnection;
    private CountDownLatch countDownLatch;
    protected volatile boolean successful;
    protected Message msg;

    AsyncHandler(OperationSubscription sub, SubscriptionConnection subscriptionConnection, SubscriptionConnection senderConnection, Message message, CountDownLatch countDownLatch, Peer peer) {
        super(sub, subscriptionConnection, peer);
        this.countDownLatch = countDownLatch;
        this.senderConnection = senderConnection;
        this.successful = false;
        this.msg = message;
        this.operation = new RetransmitMessageOperation(this.peer, msg, this.senderConnection, this);
    }

    protected void countDown() {
        if (this.countDownLatch != null)
            this.countDownLatch.countDown();
    }

    public synchronized void shutdown() {
        if (!this.successful)
            NetworkLogger.printLog(Level.SEVERE, msg.getOperation() + " protocol failed");
        this.unsubscribe();
        this.countDown();
    }

    public void startAsync() {
        this.peer.getScheduler().submit(this.operation);
    }

    public void cancel() {
        this.operation.cancel();
    }

    @Override
    public boolean wasSuccessful() {
        return this.successful;
    }

}
