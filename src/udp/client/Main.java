package udp.client;

import udp.utils.Request;

import java.net.MulticastSocket;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 4) {
            System.out.println("Usage: <multicast_name> <multicast_port_number> <operation> <args>");
        }
        String host_name = args[0];
        Integer port_number = Integer.valueOf(args[1]);
        System.out.println("Multicast Name: " + host_name);
        System.out.println("Multicast Port Number: " + port_number);
        MulticastClient client = new MulticastClient(host_name, port_number);
        Request request = new Request(args[2], Arrays.copyOfRange(args, 3, args.length));
        System.out.println("Message Received: \n" + client.sendRequest(request));
    }
}
