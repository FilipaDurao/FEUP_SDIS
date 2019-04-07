package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.handlers.ChunkSenderHandler;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.improvements.GetChunkTCPMessage;
import proj.peer.utils.RandomGenerator;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GetChunkTCPOperation implements Runnable {
    private GetChunkTCPMessage msg;
    private Peer peer;

    public GetChunkTCPOperation(GetChunkTCPMessage msg, Peer peer) {
        this.msg = msg;
        this.peer = peer;
    }

    @Override
    public void run() {
        try {
            if (this.peer.getFileManager().isChunkSaved(msg.getFileId(), msg.getChunkNo())) {
                byte[] body = this.peer.getFileManager().getChunk(msg.getFileId(), msg.getChunkNo());

                Socket socket = new Socket(InetAddress.getByName(msg.getHostname()), msg.getPort());
                DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
                stream.write(body);
                socket.close();
            }
        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Failure sending chunk - " + e.getMessage());
        }
    }
}
