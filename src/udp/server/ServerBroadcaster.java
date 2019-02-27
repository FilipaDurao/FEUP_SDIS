package udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ServerBroadcaster implements Runnable {

    private MulticastSocket adSocket;
    private String adAddress;
    private Integer adPort;
    private DatagramPacket adPacket;
    private Integer serverPort;

    ServerBroadcaster(Integer serverPort, String multicastAddress, Integer multicastPort) throws IOException {
        this.serverPort = serverPort;
        this.adAddress = multicastAddress;
        this.adPort = multicastPort;
        this.initiateAdvertiser();

    }

    @Override
    public void run() {
        System.out.println("Sending: " + new String(this.adPacket.getData()));
        try {
            this.adSocket.send(adPacket);
        } catch (IOException e) {
            System.out.println("Failed to send multicast message");
        }
    }

    private void initiateAdvertiser() throws IOException {
        // Initialize multicast socket
        this.adSocket = new MulticastSocket(this.adPort);
        this.adSocket.setLoopbackMode(false);
        this.adSocket.setTimeToLive(2);

        // Join multicast group
        this.adSocket.joinGroup(InetAddress.getByName(this.adAddress));

        String adMessage = "multicast: " + this.adAddress + " " + this.adPort + ": " + this.serverPort;
        byte[] multiBuff = new byte[1024];
        this.adPacket = new DatagramPacket(multiBuff, multiBuff.length, InetAddress.getByName(this.adAddress), this.adPort);
        this.adPacket.setData(adMessage.getBytes());
    }
}
