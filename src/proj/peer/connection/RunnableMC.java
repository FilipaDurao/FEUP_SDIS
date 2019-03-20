package proj.peer.connection;

import java.io.IOException;

public abstract class RunnableMC extends MulticastConnection implements Runnable {
    RunnableMC(String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
    }
}
