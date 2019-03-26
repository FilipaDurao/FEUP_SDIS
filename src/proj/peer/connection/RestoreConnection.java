package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.messages.ChunkMessage;

import java.io.IOException;

public class RestoreConnection extends RunnableMC {
    private Peer peer;

    public RestoreConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
    }

    @Override
    public void run() {

        while (true) {
            System.out.println("Restore awaiting message");
            try {
                ChunkMessage msg = (ChunkMessage) this.getMessage();
                if( msg.getSenderId().equals(this.peer.getPeerId()) || !msg.getVersion().equals(peer.getVersion())) {
                    continue;
                }
                System.out.println(String.format("Restore Received: %s %s %d", msg.getOperation(), msg.getFileId(), msg.getChunkNo()));


            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
