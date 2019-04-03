package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.handlers.DeleteHandler;
import proj.peer.handlers.GetChunkHandler;
import proj.peer.handlers.StoredGenericHandler;


import java.io.IOException;

public class ControlConnection extends SubscriptionConnection {

    public static final String CONNECTION_NAME = "Control";

    public ControlConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(CONNECTION_NAME, multicast_name, multicast_port_number, peer);
        this.subscribe(new StoredGenericHandler(peer, Peer.DEFAULT_VERSION));
        this.subscribe(new GetChunkHandler(peer, Peer.DEFAULT_VERSION));
        this.subscribe(new DeleteHandler(peer));
    }

}
