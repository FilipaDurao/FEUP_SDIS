package proj.peer.connection.tcp;

import proj.peer.Peer;
import proj.peer.connection.SubscriptionConnection;
import proj.peer.connection.SubscriptionConnectionInterface;
import proj.peer.subscriptions.SubscriptionManager;
import proj.peer.handlers.SubscriptionHandlerInterface;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.Message;
import proj.peer.operations.TCPMessageOperation;
import proj.peer.subscriptions.OperationSubscription;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class ServerConnection implements Runnable, SubscriptionConnectionInterface {

    private final static Integer PORT = 6748;
    private ServerSocket serverSocket;
    private Peer peer;
    protected SubscriptionManager subscriptionManager;


    public ServerConnection(Peer peer) throws IOException {
        this.peer = peer;
        this.serverSocket = new ServerSocket(this.getPort());
        this.subscriptionManager = new SubscriptionManager();
    }

    public  Integer getPort() {
        return PORT + Integer.valueOf(this.peer.getPeerId());
    }

    public void subscribe(SubscriptionHandlerInterface handler) {
        subscriptionManager.subscribe(handler);
    }

    public void unsubscribe(OperationSubscription sub) {
        subscriptionManager.unsubscribe(sub);
    }

    public boolean checkForSubscription(Message msg) {

        return subscriptionManager.checkForSubscription(msg);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                this.peer.getScheduler().submit(new TCPMessageOperation(this, socket));
            } catch (IOException e) {
                NetworkLogger.printLog(Level.WARNING, "Incoming socket connection error - " + e.getMessage());
            }
        }
    }


}
