package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.ChunkMessageTCP;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.utils.IpFinder;
import proj.peer.utils.RandomGenerator;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class GetChunkTCPOperation  implements Runnable{
    private Peer peer;
    private CountDownLatch countDownLatch;
    private GetChunkMessage msg;
    private Future future;

    public GetChunkTCPOperation(GetChunkMessage msg, Peer peer, CountDownLatch countDownLatch) {
        this.msg = msg;
        this.peer = peer;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            serverSocket.setSoTimeout(1000);
            ChunkMessageTCP response = new ChunkMessageTCP(peer.getVersion(), peer.getPeerId(), msg.getFileId(), msg.getChunkNo(), IpFinder.getIp(), serverSocket.getLocalPort());
            this.peer.getRestore().sendMessage(response);

            Socket socket = serverSocket.accept();
            byte[] body = this.peer.getFileManager().getChunk(msg.getFileId(), msg.getChunkNo());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(body);

            this.countDownLatch.countDown();

        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Error in TCP get chunk - " + e.getMessage());
        }

    }
}
