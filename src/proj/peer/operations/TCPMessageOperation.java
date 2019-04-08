package proj.peer.operations;

import proj.peer.connection.tcp.ServerConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;

public class TCPMessageOperation implements Runnable {

    private ServerConnection connection;
    private Socket socket;

    public TCPMessageOperation(ServerConnection connection, Socket socket) {
        this.connection = connection;
        this.socket = socket;
    }



    @Override
    public void run() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) inputStream.readObject();

            if (this.connection.checkForSubscription(message)) {
                return;
            }

            NetworkLogger.printLog(Level.INFO, String.format("Ignored TCP Message - %s %s", message.getOperation(), message.getSenderId()));
        } catch (IOException | ClassNotFoundException e) {
            NetworkLogger.printLog(Level.WARNING, "Failed during TCP message process - " + e.getMessage());
        }

    }
}
