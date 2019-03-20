package proj.peer.connection;

import java.io.IOException;

public class ControlConnection extends RunnableMC {

    public ControlConnection(String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
    }

    @Override
    public void run() {

    }
}
