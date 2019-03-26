package proj.peer.message.handlers.async;

import proj.peer.connection.MulticastConnection;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.message.messages.Message;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class RetransmissionHandler extends AsyncHandler {
    protected ScheduledThreadPoolExecutor scheduler;
    protected MulticastConnection senderConnection;
    protected SubscriptionConnection subscriptionConnection;
    protected Message msg;
    protected Integer attempts;
    protected volatile Boolean successful;
    protected Future future;



    public RetransmissionHandler(ScheduledThreadPoolExecutor scheduler, MulticastConnection senderConnection, SubscriptionConnection subscriptionConnection, Message msg, CountDownLatch countDownLatch) {
        super(countDownLatch);
        this.senderConnection = senderConnection;
        this.subscriptionConnection = subscriptionConnection;
        this.scheduler = scheduler;
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
            if (this.attempts < 5 && !this.successful) {
                this.future = this.scheduler.schedule(this, (long) Math.pow(2, this.attempts), TimeUnit.SECONDS);
            }
            else {
                System.err.println("Failed PUTCHUNK protocol");
                this.subscriptionConnection.unsubscribe(this.sub);
                this.countDown();
            }
        } catch (IOException e) {
            System.err.println("Error sending scheduled message");
        }

    }
}
