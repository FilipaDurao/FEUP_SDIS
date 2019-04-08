package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.handlers.async.ChunkInitiatorHandler;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.improvements.GetChunkTCPMessage;
import proj.peer.operations.RetransmitMessageOperation;
import proj.peer.utils.NullCallback;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class FileRestorerTCP extends  FileRestorer{

    private final Integer portNumber;
    private String hostname;

    FileRestorerTCP(Peer peer) throws IOException {
        super(peer);
        this.hostname = IpFinder.getIp();
        this.portNumber = this.peer.getTcprestore().getPort();
    }

    @Override
    protected byte[] restoreChunk(Integer chunkNo, String encode) throws Exception {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getPeerId(), encode, chunkNo);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ChunkInitiatorHandler handler = new ChunkInitiatorHandler(peer, msg, countDownLatch);
        this.peer.getTcprestore().subscribe(handler);
        handler.startAsync();
        countDownLatch.await();
        if (!handler.wasSuccessful())
            throw new Exception("Chunk retrieval not successful.");

        return handler.getBody();
    }
}
