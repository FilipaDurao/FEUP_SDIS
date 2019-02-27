package udp.client;

import udp.utils.Request;

import java.io.IOException;
import java.net.*;

public class Client {
    public static final int TRIES = 3;
    private DatagramSocket socket;
    private String host_name;
    private Integer port_number;


    Client(String host_name, Integer port_number) throws IOException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(1000);
        this.host_name = host_name;
        this.port_number = port_number;
    }

    private void sendSingleRequest(Request request) throws IOException {
        byte[] requestBytes = request.getBytes();
        DatagramPacket packet = new DatagramPacket(requestBytes, requestBytes.length, InetAddress.getByName(this.host_name), this.port_number);
        this.socket.send(packet);
    }

    private String getResponse() throws IOException {
        byte[] responseBuff = new byte[1024];
        DatagramPacket response = new DatagramPacket(responseBuff, responseBuff.length);
        socket.receive(response);
        return new String(response.getData(), 0, response.getLength());
    }

    String sendRequest(Request request) throws Exception {
        int tries = TRIES;

        while (tries > 0) {
            try {
                this.sendSingleRequest(request);
                System.out.println("Message Sent: \n" + request);
                return this.getResponse();
            } catch (SocketTimeoutException e) {
                tries--;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                break;
            }
        }

        throw new Exception("Unable to send message");
    }

}
