import java.io.IOException;
import java.net.*;

public class Client {
    private DatagramSocket socket;
    private String host_name;
    private Integer port_number;
    private String oper;
    private String[] opnd;


    Client(String host_name, Integer port_number, String oper, String[] opnd) throws IOException {
        this.socket = new DatagramSocket();
        this.host_name = host_name;
        this.port_number = port_number;
        this.oper = oper;
        this.opnd = opnd;


        //register();
        //lookup();

    }

    public void register() throws IOException {
        String lincenseAndOwner = this.opnd[0] + " " + this.opnd[1];
        DatagramPacket packet = new DatagramPacket(lincenseAndOwner.getBytes(), lincenseAndOwner.length(), InetAddress.getByName(this.host_name), this.port_number);
        socket.send(packet);
    }

    public void lookup() throws IOException {
        String lincensePlate = this.opnd[0];
        DatagramPacket packet = new DatagramPacket(lincensePlate.getBytes(), lincensePlate.length(), InetAddress.getByName(this.host_name), this.port_number);
        socket.send(packet);
    }

}
