package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.message.messages.StoredMessageTCP;
import proj.peer.utils.IpFinder;
import proj.peer.utils.RandomGenerator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PutChunkTCPOperation implements Runnable {
    private PutChunkMessage msg;
    private Peer peer;
    private ServerSocket serverSocket;
    private Socket socket;

    public PutChunkTCPOperation(PutChunkMessage msg, Peer peer) {
        this.msg = msg;
        this.peer = peer;
    }

    @Override
    public void run() {
        try {

            // Open server socket
            serverSocket = new ServerSocket(0);
            serverSocket.setSoTimeout(3000);

            // Send message
            StoredMessageTCP msg = new StoredMessageTCP(this.peer.getVersion(), this.peer.getPeerId(), this.msg.getFileId(), this.msg.getChunkNo(), IpFinder.getIp(), serverSocket.getLocalPort());
            this.peer.getScheduler().submit(new SendMessageOperation(this.peer.getControl(), msg));

            // Get Message
            socket = serverSocket.accept();
            ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
            byte[] body = (byte[]) stream.readObject();

            try {
                // Save body
                this.peer.getFileManager().putChunk(this.msg.getFileId(), this.msg.getChunkNo(), body, this.msg.getReplicationDegree());
                int delay = RandomGenerator.getNumberInRange(0, 400);
                this.peer.getScheduler().schedule(new SendMessageOperation(this.peer.getControl(), new StoredMessage(this.peer.getPeerId(), this.msg.getFileId(), this.msg.getChunkNo())), delay, TimeUnit.MILLISECONDS);
                new ObjectOutputStream(socket.getOutputStream()).writeObject(true);
            } catch (Exception e) {
                NetworkLogger.printLog(Level.WARNING, "Error saving chunk in peer - " + e.getMessage());
                new ObjectOutputStream(socket.getOutputStream()).writeObject(false);
            }

        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Failure put chunk tcp operation - " + e.getMessage());
        }

        // Close sockets
        this.closeSockets();
    }


    public void closeSockets() {
        try {
            if (this.serverSocket != null)
                this.serverSocket.close();

            if (this.socket != null)
                this.socket.close();
        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Closing in put chunk tcp operation sockets - " + e.getMessage());
        }
    }
}
