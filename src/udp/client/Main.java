package udp.client;

import udp.client.Client;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        Client client = new Client(args[0], Integer.valueOf(args[1]), args[2], Arrays.copyOfRange(args, 3, args.length));
    }
}
