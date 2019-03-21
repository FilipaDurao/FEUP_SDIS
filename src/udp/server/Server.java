package udp.server;

import udp.server.operations.Operation;
import udp.utils.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Server {

    private static final int MAX_MESSAGE_SIZE = 1024;
    private static final Boolean DEBUG_LOG = true;

    private DatagramSocket socket;
    private DatagramPacket packet;

    private HashMap<String, Operation> operations;

    private ScheduledThreadPoolExecutor executor;

    Server(Integer portNumber, String adAddress, Integer adPort) throws IOException {
        this.executor = new ScheduledThreadPoolExecutor(3);
        this.executor.scheduleAtFixedRate(new ServerBroadcaster(portNumber, adAddress, adPort), 0, 1, TimeUnit.SECONDS);

        // Start on portNumber
        this.socket = new DatagramSocket(portNumber);

        // Setting up arriving packet placeholder
        byte[] msgBuf = new byte[MAX_MESSAGE_SIZE];
        this.packet = new DatagramPacket(msgBuf, msgBuf.length);

        this.operations = new HashMap<>();
    }

    void run() throws IOException {
        while (true) {
            // Receive Request
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
