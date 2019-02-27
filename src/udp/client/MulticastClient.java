package udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient extends Client {

    private String multicast_name;
    private Integer multicast_port_number;

    MulticastClient(String multicast_name, Integer multicast_port_number) throws IOException {
        super("", 0);
        this.multicast_name = multicast_name;
        this.multicast_port_number = multicast_port_number;
        this.multicastCommunication();
    }

    void multicastCommunication() throws IOException {
        MulticastSocket mcstSocket = new MulticastSocket(this.multicast_port_number);
        mcstSocket.joinGroup(InetAddress.getByName(this.multicast_name));

        byte[] buf = new byte[1024];
        DatagramPacket response = new DatagramPacket(buf, buf.length);

        mcstSocket.receive(response);
        this.host_name = String.valueOf(response.getAddress()).substring(1);
        String responseString = new String(response.getData(), 0, response.getLength());
        Integer spaceIndex = responseString.lastIndexOf(" ");
        this.port_number = Integer.valueOf(responseString.substring(spaceIndex + 1));
        System.out.println(responseString);
    }
}
