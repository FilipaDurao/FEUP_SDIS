package proj.peer.connection;

import proj.peer.Peer;

import java.io.IOException;

public class RestoreConnection extends SubscriptionConnection {

    public static final String CONNECTION_NAME = "Restore";

    public RestoreConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(CONNECTION_NAME, multicast_name, multicast_port_number, peer);
    }

}
