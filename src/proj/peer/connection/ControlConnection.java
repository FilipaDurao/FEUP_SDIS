package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.messages.Message;


import java.io.IOException;

public class ControlConnection extends SubscriptionConnection {

    public ControlConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number, peer);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message msg = this.getMessage();

                if (msg.getSenderId().equals(peer.getPeerId()) || !msg.getVersion().equals(peer.getVersion())) {
                    continue;
                }

                if(checkForSubscription(msg)) {
                    continue;
                }


                System.out.println(String.format("Message Ignored: %s %s %s", msg.getOperation(), msg.getSenderId(), msg.getFileId()));
                System.err.println("TODO: Add treatment for messages of files/chunks saved in this computer");
                System.err.println("HINT: Add a handler for each message");

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
