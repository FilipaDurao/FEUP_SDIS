package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.utils.CallbackInterface;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class RetransmitMessageOperation implements Runnable {
    private Peer peer;
    private Message msg;
    private SubscriptionConnection senderConnection;
    private CallbackInterface callbackInterface;
    private int attempts;
    private boolean successful;
    private Future future;

    public RetransmitMessageOperation(Peer peer, Message msg, SubscriptionConnection senderConnection, CallbackInterface callbackInterface) {
        this.peer = peer;
        this.msg = msg;
        this.senderConnection = senderConnection;
        this.callbackInterface = callbackInterface;
        this.attempts = 0;
        this.successful = false;
    }

    @Override
    public void run() {
        try {
            this.senderConnection.sendMessage(msg);
            NetworkLogger.printLog(Level.INFO, "Message sent - " + msg.getOperation() + " " + msg.getTruncatedFilename(), this.senderConnection.getConnectionName());
            this.attempts++;
        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Error sending scheduled message - " + e.getMessage());
        }
        if (this.attempts < 5 && !this.successful) {
            this.future = this.peer.getScheduler().schedule(this, (long) Math.pow(2, this.attempts), TimeUnit.SECONDS);
        } else {
            this.callbackInterface.callback();
        }
    }

    public void cancel() {
        this.future.cancel(true);
    }
}
