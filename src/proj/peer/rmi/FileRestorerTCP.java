package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.message.messages.improvements.GetChunkTCPMessage;
import proj.peer.operations.RetransmitMessageOperation;
import proj.peer.utils.NullCallback;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class FileRestorerTCP extends  FileRestorer{

    private ServerSocket serverSocket;
    private String hostname;
    public final static Integer PORT = 6748;
    public static final Integer timeout = 30 * 1000;

    FileRestorerTCP(Peer peer) throws IOException {
        super(peer);
        this.serverSocket = new ServerSocket(PORT + Integer.valueOf(peer.getPeerId()));
        this.serverSocket.setSoTimeout(timeout);
        this.hostname = IpFinder.getIp();
    }

    @Override
    protected byte[] restoreChunk(Integer chunkNo, String encode) throws Exception {
        System.out.println(String.format("Trying message %s %d", encode.substring(0, 5), chunkNo));
        GetChunkTCPMessage message = new GetChunkTCPMessage(this.peer.getVersion(), this.peer.getPeerId(), encode, chunkNo, this.hostname, this.serverSocket.getLocalPort());
        RetransmitMessageOperation operation = new RetransmitMessageOperation(this.peer, message, this.peer.getControl(), new NullCallback());
        this.peer.getScheduler().submit(operation);

        try {
            Socket socket = this.serverSocket.accept();
            operation.cancel();

            byte[] buffer = new byte[MulticastConnection.CHUNK_SIZE];
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            inputStream.readFully(buffer);
            //System.out.println(String.format("Read %d bytes", nBytes));

            socket.close();
            return buffer;
            //return Arrays.copyOfRange(buffer, 0, nBytes);

        } catch (SocketTimeoutException e) {
            operation.cancel();
        }

        throw new Exception("Chunk retrieval not successful.");
    }
}
