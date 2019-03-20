package proj.peer.connection;

import proj.peer.message.Message;
import proj.peer.message.MessageFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastConnection {

    protected MulticastSocket multiSocket;
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
        this.multiSocket.send(new DatagramPacket(msgBytes,msgBytes.length, InetAddress.getByName(multicast_name), multicast_port_number));
    }

    public Message getMessage()  {
        byte[] msgBytes = new byte[1024];
        DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length);

        try {
            this.multiSocket.receive(packet);
            return MessageFactory.getMessage(new String(packet.getData(), 0, packet.getLength()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
