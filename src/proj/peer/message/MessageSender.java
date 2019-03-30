package proj.peer.message;

import proj.peer.connection.MulticastConnection;
import proj.peer.message.messages.Message;

import java.io.IOException;

public class MessageSender implements Runnable  {

    private MulticastConnection connection;
    private Message msg;

    public MessageSender(MulticastConnection connection, Message msg) {

        this.connection = connection;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            this.connection.sendMessage(this.msg);
        } catch (IOException e) {
            System.err.println("[Error sending message] : " + e.getMessage());
        }
    }
}
