package proj.peer.operations;

import proj.peer.connection.MulticastConnection;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;

import java.io.IOException;
import java.util.logging.Level;

public class SendMessageOperation implements Runnable  {

    private SubscriptionConnection connection;
    private Message msg;

    public SendMessageOperation(SubscriptionConnection connection, Message msg) {

        this.connection = connection;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            this.connection.sendMessage(this.msg);
            if (this.msg instanceof MessageChunk) {
                NetworkLogger.printLog(Level.INFO, "Message sent - " + this.msg.getOperation() + " " + this.msg.getTruncatedFilename() + " " + ((MessageChunk) this.msg).getChunkNo(), this.connection.getConnectionName());
            } else {
                NetworkLogger.printLog(Level.INFO, "Message sent - " + this.msg.getOperation() + " " + this.msg.getTruncatedFilename(), this.connection.getConnectionName());
            }
        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Error sending " + this.msg.getOperation() + " message - " + e.getMessage(), this.connection.getConnectionName());
        }
    }
}
