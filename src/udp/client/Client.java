package udp.client;

import udp.utils.Request;

import java.io.IOException;
import java.net.*;

public class Client {
    private DatagramSocket socket;
    private String host_name;
    private Integer port_number;


    Client(String host_name, Integer port_number) throws IOException {
        this.socket = new DatagramSocket();
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
        try {
            this.sendSingleRequest(request);
            System.out.println("Message Sent: \n" + request);
            return this.getResponse();
        } catch (UnknownHostException e) {
            System.err.println("Exception: Unknown Host");
        } catch (IOException e) {
            System.err.println("Unable to send request or retrieve response");
        }

        throw new Exception("Unable to send message");
    }

}
