package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.handlers.subscriptions.OperationSubscription;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class RetransmissionHandler extends AsyncHandler {
    protected ScheduledThreadPoolExecutor scheduler;
    protected MulticastConnection senderConnection;
    protected Message msg;
    protected Integer attempts;
    protected volatile Boolean successful;
    protected volatile Future future;


    public RetransmissionHandler(OperationSubscription sub, Peer peer, MulticastConnection senderConnection, SubscriptionConnection subscriptionConnection, Message msg, CountDownLatch countDownLatch) {
        super(sub, subscriptionConnection, peer, countDownLatch);
        this.senderConnection = senderConnection;
        this.scheduler = peer.getScheduler();
        this.msg = msg;
        this.attempts = 0;
        this.successful = false;
    }

    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }


    @Override
    public void run() {
        try {
            this.senderConnection.sendMessage(msg);
            this.attempts++;
        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Error sending scheduled message - " + e.getMessage());
        }
        if (this.attempts < 5 && !this.successful) {
            this.future = this.scheduler.schedule(this, (long) Math.pow(2, this.attempts), TimeUnit.SECONDS);
        } else {
            NetworkLogger.printLog(Level.SEVERE, msg.getOperation() + " protocol failed");
            this.unsubscribe();
            this.countDown();
        }


    }
}
