package proj.peer.connection;

import proj.peer.message.Message;

import java.io.IOException;

public class DataBackup extends RunnableMC {
    public DataBackup(String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Awaiting message");
            try {
                Message msg = this.getMessage();
                System.out.println(msg);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
