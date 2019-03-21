package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.PutChunkMessage;
import proj.peer.message.StoredMessage;

import java.io.IOException;

public class BackupConnection extends RunnableMC {
    private Peer peer;

    public BackupConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
    }

    @Override
    public void run() {
        System.out.println("Awaiting message");

        while (true) {
            try {
                PutChunkMessage msg = (PutChunkMessage) this.getMessage();
                if( msg.getSenderId().equals(this.peer.getPeerId())) {
                    continue;
                }
                System.out.println("Received Message");

                this.peer.getFileManager().putChunk(msg.getFileId(), msg.getChunkNo(), msg.getBody(), msg.getReplicationDegree());

                StoredMessage response = new StoredMessage(peer.getPeerId(), msg.getFileId(), msg.getChunkNo());
                peer.getControl().sendMessage(response);

                System.out.println("Awaiting message");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
