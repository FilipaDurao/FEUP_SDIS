package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.handlers.async.StoredInitiatorTCPHandler;
import proj.peer.log.NetworkLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;

public class StoredTCPOperation implements Runnable {

    private boolean success;
    private Peer peer;
    private String peerId;
    private String fileId;
    private String pathname;
    private Integer chunkNo;
    private String hostname;
    private Integer port;
    private StoredInitiatorTCPHandler handler;

    public StoredTCPOperation(Peer peer, String peerId, String fileId, String pathname, Integer chunkNo, String hostname, Integer port, StoredInitiatorTCPHandler handler) {
        this.peer = peer;
        this.peerId = peerId;
        this.fileId = fileId;
        this.pathname = pathname;
        this.chunkNo = chunkNo;
        this.hostname = hostname;
        this.port = port;
        this.handler = handler;
        this.success = false;
    }

    @Override
    public void run() {
            RandomAccessFile file = null;
        try {
            // Read from file
            byte[] buffer = new byte[MulticastConnection.CHUNK_SIZE];
            int length = 0;
            file = new RandomAccessFile(this.pathname, "r");
            int startingPoint = this.chunkNo * MulticastConnection.CHUNK_SIZE;
            if (startingPoint < file.length()) {
                file.seek(startingPoint);
                length = file.read(buffer);
            }

            this.peer.getFileManager().setChunkSize(this.fileId, this.chunkNo, length);
            // Send chunk
            Socket socket = new Socket(InetAddress.getByName(this.hostname), this.port);
            socket.setKeepAlive(true);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(Arrays.copyOfRange(buffer, 0, length));
            if(  (boolean) new ObjectInputStream(socket.getInputStream()).readObject()) {
                this.peer.getFileManager().storeChunkPeer(fileId, chunkNo, peerId);
                this.handler.markSuccess(peerId);
                this.success = true;
            }
            file.close();
            socket.close();
        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Failure sending PUTCHUNK through TCP - " + e.getMessage());
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e1) {
                    NetworkLogger.printLog(Level.WARNING, "Error closing file - " + e.getMessage());
                }
            }
        }
        if (!this.success)
            this.handler.markFailure();

    }
}
