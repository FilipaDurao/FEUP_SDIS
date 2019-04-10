package proj.peer.operations;

import proj.peer.connection.MulticastConnection;
import proj.peer.handlers.async.StoredInitiatorTCPHandler;
import proj.peer.log.NetworkLogger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;

public class StoredTCPOperation implements Runnable {

    private boolean success;
    private String pathname;
    private Integer chunkNo;
    private String hostname;
    private Integer port;
    private StoredInitiatorTCPHandler handler;

    public StoredTCPOperation(String pathname, Integer chunkNo, String hostname, Integer port, StoredInitiatorTCPHandler handler) {

        this.pathname = pathname;
        this.chunkNo = chunkNo;
        this.hostname = hostname;
        this.port = port;
        this.handler = handler;
        this.success = false;
    }

    @Override
    public void run() {
        try {
            // Read from file
            byte[] buffer = new byte[MulticastConnection.CHUNK_SIZE];
            RandomAccessFile file = new RandomAccessFile(this.pathname, "r");
            file.seek(this.chunkNo * MulticastConnection.CHUNK_SIZE);
            int length = file.read(buffer);

            // Send chunk
            Socket socket = new Socket(InetAddress.getByName(this.hostname), this.port);
            socket.setKeepAlive(true);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(Arrays.copyOfRange(buffer, 0, length));
            if(  (boolean) new ObjectInputStream(socket.getInputStream()).readObject()) {
                this.handler.markSuccess();
                this.success = true;
            }

            socket.close();
        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Failure sending PUTCHUNK through TCP - " + e.getMessage());
        }
        if (!this.success)
            this.handler.markFailure();

    }
}
