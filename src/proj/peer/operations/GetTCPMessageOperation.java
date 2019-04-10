package proj.peer.operations;

import proj.peer.handlers.BodyReceiver;
import proj.peer.log.NetworkLogger;

import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;

public class GetTCPMessageOperation implements Runnable {
    private String hostname;
    private Integer port;
    private BodyReceiver bodyReceiver;

    public GetTCPMessageOperation(String hostname, Integer port, BodyReceiver bodyReceiver) {

        this.hostname = hostname;
        this.port = port;
        this.bodyReceiver = bodyReceiver;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(InetAddress.getByName(this.hostname), this.port);
            ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
            byte[] message = (byte[]) stream.readObject();
            this.bodyReceiver.setBody(message);
        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Error retrieving TCP message - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
