package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.handlers.async.PutChunkHandler;
import proj.peer.message.messages.Message;
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

        System.out.println("Backup awaiting message");
        while (true) {

            try {
                Message message = this.getMessage();
                if( message.getSenderId().equals(this.peer.getPeerId()) || !(message instanceof PutChunkMessage) || !message.getVersion().equals(peer.getVersion())) {
                    continue;
                }

                PutChunkMessage msg = (PutChunkMessage) message;

                System.out.println(String.format("Backup Received: %s %s %d", msg.getOperation(), msg.getFileId(), msg.getChunkNo()));

                this.peer.getScheduler().execute(new PutChunkHandler(msg, peer));
            } catch (Exception e) {
                System.err.println("Backup Connection Error: " + e.getMessage());
            }
        }
    }

}
