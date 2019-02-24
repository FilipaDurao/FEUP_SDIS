package udp.server;

import udp.server.operations.plates.LookupPlate;
import udp.server.operations.plates.RegisterPlate;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ServerMain <port_number>");
        }


        try {
            int portNumber = Integer.valueOf(args[0]);
            Server server = new Server(portNumber);
            server.addOperation("lookup", new LookupPlate());
            server.addOperation("register", new RegisterPlate());
            server.run();
        } catch (IOException e) {
            System.out.println("Exited");
        }
    }
}
