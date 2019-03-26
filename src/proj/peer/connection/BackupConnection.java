package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.messages.PutChunkMessage;
import proj.peer.message.messages.StoredMessage;
import proj.peer.message.MessageSender;
import proj.peer.utils.RandomGenerator;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BackupConnection extends RunnableMC {
    private Peer peer;

    public BackupConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
    }

    @Override
    public void run() {

        while (true) {
            System.out.println("Backup awaiting message");

            try {
                PutChunkMessage msg = (PutChunkMessage) this.getMessage();
                if( msg.getSenderId().equals(this.peer.getPeerId()) || !msg.getVersion().equals(peer.getVersion())) {
                    continue;
                }
                System.out.println(String.format("Backup Received: %s %s %d", msg.getOperation(), msg.getFileId(), msg.getChunkNo()));

                this.peer.getFileManager().putChunk(msg.getFileId(), msg.getChunkNo(), msg.getBody(), msg.getReplicationDegree());

                StoredMessage response = new StoredMessage(peer.getVersion(), peer.getPeerId(), msg.getFileId(), msg.getChunkNo());
                int delay = RandomGenerator.getNumberInRange(0, 400);
                this.peer.getScheduler().schedule(new MessageSender(peer.getControl(), response), delay, TimeUnit.MILLISECONDS);

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
