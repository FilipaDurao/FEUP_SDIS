package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.Message;
import proj.peer.message.PutChunkMessage;
import proj.peer.message.StoredMessage;

import java.io.IOException;

public class DataBackup extends RunnableMC {
    private Peer peer;

    public DataBackup(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Awaiting message");
            try {
                PutChunkMessage msg = (PutChunkMessage) this.getMessage();
                System.out.println("Received Message");

                // Save chunk

                StoredMessage response = new StoredMessage(peer.getPeerId(), msg.getFileId(), msg.getChunkNo());
                peer.getControl().sendMessage(response);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
