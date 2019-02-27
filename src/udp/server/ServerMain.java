package udp.server;

import udp.server.operations.plates.LookupPlate;
import udp.server.operations.plates.RegisterPlate;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java ServerMain <port_number> <multicast_address> <multicast_port>");
        }


        try {
            int portNumber = Integer.valueOf(args[0]);
            int multicastPortNumber = Integer.valueOf(args[2]);
            System.out.println("Running on port: " + portNumber);
            Server server = new Server(portNumber, args[1], multicastPortNumber);
            server.addOperation("lookup", new LookupPlate());
            server.addOperation("register", new RegisterPlate());
            server.run();
        } catch (IOException e) {
            System.out.println("Exited");
            e.printStackTrace();
        }
    }
}
