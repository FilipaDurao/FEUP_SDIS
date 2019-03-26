package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.messages.ChunkMessage;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.PutChunkMessage;

import java.io.IOException;

public class RestoreConnection extends RunnableMC {
    private Peer peer;

    public RestoreConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
    }

    @Override
    public void run() {

        System.out.println("Restore awaiting message");
        while (true) {
            try {
                Message message = this.getMessage();
                if( message.getSenderId().equals(this.peer.getPeerId())|| !(message instanceof ChunkMessage) || !message.getVersion().equals(peer.getVersion())) {
                    continue;
                }
                ChunkMessage msg = (ChunkMessage) message;

                System.out.println(String.format("Restore Received: %s %s %d", msg.getOperation(), msg.getFileId(), msg.getChunkNo()));

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
