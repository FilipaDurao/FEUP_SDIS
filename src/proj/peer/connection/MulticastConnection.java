package proj.peer.connection;

import proj.peer.message.messages.Message;
import proj.peer.message.MessageFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MulticastConnection {

    public static final int CHUNK_SIZE = 64000;
    public static final int MAX_HEADER_SIZE = 1000;
    protected MulticastSocket multiSocket;
    private String multicast_name;
    private Integer multicast_port_number;

    MulticastConnection(String multicast_name, Integer multicast_port_number) throws IOException {
        this.multicast_name = multicast_name;
        this.multicast_port_number = multicast_port_number;
        this.multiSocket = new MulticastSocket(this.multicast_port_number);
        multiSocket.joinGroup(InetAddress.getByName(this.multicast_name));
        // multiSocket.setLoopbackMode(false);
        // multiSocket.setTimeToLive(1);
    }

    public void sendMessage(Message msg) throws IOException {
        byte[] msgBytes = msg.getBytes();
        this.multiSocket.send(new DatagramPacket(msgBytes,msgBytes.length, InetAddress.getByName(multicast_name), multicast_port_number));
    }

    public Message getMessage() throws Exception {
        byte[] msgBytes = new byte[CHUNK_SIZE + MAX_HEADER_SIZE];
        DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length);

        this.multiSocket.receive(packet);
        return MessageFactory.getMessage(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
    }
    
}
