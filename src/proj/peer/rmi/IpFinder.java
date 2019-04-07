package proj.peer.rmi;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class IpFinder {

    public static String getIp() throws SocketException, UnknownHostException {
        final DatagramSocket socket = new DatagramSocket();
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);

        return socket.getLocalAddress().getHostAddress();

    }
}
