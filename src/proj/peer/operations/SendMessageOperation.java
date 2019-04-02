package proj.peer.operations;

import proj.peer.connection.MulticastConnection;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;

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
        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Error sending message - " + e.getMessage(), this.connection.getConnectionName());
        }
    }
}
