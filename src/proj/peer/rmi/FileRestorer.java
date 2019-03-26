package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.utils.SHA256Encoder;

import java.io.IOException;

public class FileRestorer {
    /**
     * Peer associated with the sender.
     */
    private Peer peer;

    FileRestorer(Peer peer) {
        this.peer = peer;
    }

    public boolean restoreFile(String filename) {
        try {

            for (int i = 0;;i++) {
                String body = restoreChunk(filename);
                if (body.length() < MulticastConnection.CHUNK_SIZE) {
                    break;
                }
            }

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private String restoreChunk(String filename) throws IOException {
        GetChunkMessage msg = new GetChunkMessage(this.peer.getVersion(), this.peer.getPeerId(), SHA256Encoder.encode(filename), 0);
        this.peer.getControl().sendMessage(msg);
        return "";
    }
}
