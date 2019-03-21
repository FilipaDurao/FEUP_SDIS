package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.message.PutChunkMessage;
import proj.peer.message.handlers.PutChunkHandler;
import proj.peer.utils.SHA256Encoder;

import java.io.File;

public class ChunkSender {

    private Peer peer;

    public ChunkSender(Peer peer) {
        this.peer = peer;
    }

    void sendChunk(Integer replicationDegree, File file, String body, int i) {
        String encodedFileName = SHA256Encoder.encode(file.getName());
        PutChunkMessage msg = new PutChunkMessage(peer.getPeerId(), encodedFileName, i, replicationDegree, body);
        PutChunkHandler handler = new PutChunkHandler(this.peer, msg);
        handler.run();
        this.peer.getControl().subscribe(handler);
    }
}