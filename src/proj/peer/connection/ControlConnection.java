package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.handlers.*;


import java.io.IOException;

public class ControlConnection extends SubscriptionConnection {

    public static final String CONNECTION_NAME = "Control";

    public ControlConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(CONNECTION_NAME, multicast_name, multicast_port_number, peer);
        this.subscribe(new StoredGenericHandler(peer, this));
        this.subscribe(new GetChunkHandler(peer, this));
        this.subscribe(new DeleteHandler(peer, this));
        this.subscribe(new RemovedHandler(peer, this));

        if (!this.peer.getVersion().equals(Peer.DEFAULT_VERSION)) {
            this.subscribe(new GetChunkTCPHandler(this, peer));
        }
    }

}
