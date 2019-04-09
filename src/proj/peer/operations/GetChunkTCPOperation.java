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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GetChunkTCPOperation  implements Runnable{
    private Peer peer;
    private GetChunkMessage msg;

    public GetChunkTCPOperation(GetChunkMessage msg, Peer peer) {
        this.msg = msg;
        this.peer = peer;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            serverSocket.setSoTimeout(1000);
            ChunkMessageTCP response = new ChunkMessageTCP(peer.getVersion(), peer.getPeerId(), msg.getFileId(), msg.getChunkNo(), IpFinder.getIp(), serverSocket.getLocalPort());
            int delay = RandomGenerator.getNumberInRange(0, 400);
            Future future = this.peer.getScheduler().schedule(new SendMessageOperation(peer.getRestore(), response), delay, TimeUnit.MILLISECONDS);

            Socket socket = serverSocket.accept();
            byte[] body = this.peer.getFileManager().getChunk(msg.getFileId(), msg.getChunkNo());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(body);

        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Error in TCP get chunk - " + e.getMessage());
        }

    }
}
