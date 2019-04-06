package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.handlers.PutChunkHandler;

import java.io.IOException;

public class BackupConnection extends SubscriptionConnection {

    public static final String CONNECTION_NAME = "Backup";

    public BackupConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(CONNECTION_NAME, multicast_name, multicast_port_number, peer);
        this.subscribe(new PutChunkHandler(peer, this));
    }

}
