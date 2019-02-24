package udp.server;

import udp.server.operations.Operation;
import udp.utils.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

class Server {

    private static final int MAX_MESSAGE_SIZE = 1024;
    private static final Boolean DEBUG_LOG = true;

    private DatagramSocket socket;
    private DatagramPacket packet;
    private HashMap<String, Operation> operations;

    Server(Integer portNumber) throws SocketException {
        // Start on portNumber
        this.socket = new DatagramSocket(portNumber);

        // Setting up arriving packet placeholder
        byte[] msgBuf = new byte[MAX_MESSAGE_SIZE];
        this.packet = new DatagramPacket(msgBuf, msgBuf.length);

        this.operations = new HashMap<>();
    }

    void run() throws IOException {
        while (true) {
            //Receive Request
            socket.receive(packet);
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


}
