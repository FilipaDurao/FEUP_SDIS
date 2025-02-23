package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.handlers.async.AsyncHandler;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class RetransmitMessageOperation implements Runnable {
    private Peer peer;
    private Message msg;
    private SubscriptionConnection senderConnection;
    private AsyncHandler asyncHandler;
    private int attempts;
    private boolean successful;
    private Future future;

    public RetransmitMessageOperation(Peer peer, Message msg, SubscriptionConnection senderConnection, AsyncHandler asyncHandler) {
        this.peer = peer;
        this.msg = msg;
        this.senderConnection = senderConnection;
        this.asyncHandler = asyncHandler;
        this.attempts = 0;
        this.successful = false;
    }

    @Override
    public void run() {
        if (this.attempts < 5 && !this.successful) {
            this.future = this.peer.getScheduler().schedule(this, (long) Math.pow(2, this.attempts), TimeUnit.SECONDS);
        } else {
            this.asyncHandler.shutdown();
            return;
        }

        try {
            this.attempts++;

            this.senderConnection.sendMessage(msg);
            if (this.msg instanceof MessageChunk) {
                NetworkLogger.printLog(Level.INFO, "Message sent - " + this.msg.getOperation() + " " + this.msg.getTruncatedFilename() + " " + ((MessageChunk) this.msg).getChunkNo(), this.senderConnection.getConnectionName());
            } else {
                NetworkLogger.printLog(Level.INFO, "Message sent - " + this.msg.getOperation() + " " + this.msg.getTruncatedFilename(), this.senderConnection.getConnectionName());
            }

        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Error sending scheduled message - " + e.getMessage());
        }

    }

    public void cancel() {
        this.future.cancel(true);
    }
}
