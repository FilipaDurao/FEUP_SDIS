package proj.peer.connection;

import proj.peer.message.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastConnection {

    private final MulticastSocket multiSocket;
    private String multicast_name;
    private Integer multicast_port_number;

    MulticastConnection(String multicast_name, Integer multicast_port_number) throws IOException {
        this.multicast_name = multicast_name;
        this.multicast_port_number = multicast_port_number;
        this.multiSocket = new MulticastSocket(this.multicast_port_number);
        multiSocket.joinGroup(InetAddress.getByName(this.multicast_name));
    }

    public void sendMessage(Message msg) throws IOException {
        byte[] msgBytes = msg.getBytes();
        this.multiSocket.send(new DatagramPacket(msgBytes,msgBytes.length));
    }

    public Message getMessage() throws Exception {
        byte[] msgBytes = new byte[1024];
        DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length);

        this.multiSocket.receive(packet);
        return new Message(new String(packet.getData(), 0, packet.getLength()));
    }

    public String getMulticast_name() {
        return multicast_name;
    }

    public void setMulticast_name(String multicast_name) {
        this.multicast_name = multicast_name;
    }

    public Integer getMulticast_port_number() {
        return multicast_port_number;
    }

    public void setMulticast_port_number(Integer multicast_port_number) {
        this.multicast_port_number = multicast_port_number;
    }
}
