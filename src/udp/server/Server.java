package udp.server;

import udp.server.operations.Operation;
import udp.utils.Request;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

class Server {

    private static final int MAX_MESSAGE_SIZE = 1024;
    private static final Boolean DEBUG_LOG = true;

    private MulticastSocket adSocket;
    private String adAddress;
    private Integer adPort;
    private DatagramPacket adPacket;

    private DatagramSocket socket;
    private DatagramPacket packet;
    private Integer portNumber;

    private HashMap<String, Operation> operations;

    Server(Integer portNumber, String adAddress, Integer adPort) throws IOException {
        this.portNumber = portNumber;
        this.adAddress = adAddress;
        this.adPort = adPort;

        // Start on portNumber
        this.socket = new DatagramSocket(portNumber);
        this.socket.setSoTimeout(1000);

        // Setting up arriving packet placeholder
        byte[] msgBuf = new byte[MAX_MESSAGE_SIZE];
        this.packet = new DatagramPacket(msgBuf, msgBuf.length);

        this.operations = new HashMap<>();

        // Initiate multicast socket
        this.initiateAdvertiser();
    }

    void run() throws IOException {
        while (true) {
            // Advertise service
            this.advertiseService();
            //Receive Request
            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                continue;
            }

            String request = new String(packet.getData(), 0, packet.getLength());

            if (DEBUG_LOG)
                System.out.println("Received: \n" + request);

            String response = this.getResponse(request);

            if (DEBUG_LOG)
                System.out.println("Response: \n" + response);

            packet.setData(response.getBytes());
            socket.send(packet);
        }
    }



    private String getResponse(String requestString) {
        Request request = new Request(requestString);
        if (this.operations.containsKey(request.getOperation()))
            return this.operations.get(request.getOperation()).perform(request);
        else
            return "Operation not supported";
    }

    boolean addOperation(String operationName, Operation operation) {
        if (this.operations.containsKey(operationName.toUpperCase())) {
            return false;
        }

        this.operations.put(operationName.toUpperCase(), operation);
        return true;

    }

    private void initiateAdvertiser() throws IOException {
        // Initialize multicast socket
        this.adSocket = new MulticastSocket(this.adPort);
        this.adSocket.setLoopbackMode(false);
        this.adSocket.setTimeToLive(2);

        // Join multicast group
        this.adSocket.joinGroup(InetAddress.getByName(this.adAddress));

        String adMessage = "multicast: " + this.adAddress + " " + this.adPort + ": " + this.portNumber;
        byte[] multiBuff = new byte[1024];
        this.adPacket = new DatagramPacket(multiBuff, multiBuff.length, InetAddress.getByName(this.adAddress), this.adPort);
        this.adPacket.setData(adMessage.getBytes());
    }

    private void advertiseService() throws IOException {
        System.out.println("Sending: " + new String(this.adPacket.getData()));
        this.adSocket.send(adPacket);
    }

}
