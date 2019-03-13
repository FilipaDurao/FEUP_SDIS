package proj.peer.connection;

import proj.peer.message.Message;

import java.io.IOException;

public class DataBackupSender {
    private static DataBackupSender ourInstance = new DataBackupSender();

    public static DataBackupSender getInstance() {
        return ourInstance;
    }

    public final static String MULTICAST_ADDER = "230.1.2.3";
    public final static Integer MULTICAST_PORT = 5678;

    private MulticastConnection connection;
    private Boolean connectionEstablished;

    private DataBackupSender() {

        try {
            this.connection = new MulticastConnection(MULTICAST_ADDER, MULTICAST_PORT);
            this.connectionEstablished = true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            this.connectionEstablished = false;
        }

    }

    public void sendMessage(Message msg) throws Exception {
        if(!connectionEstablished)
            throw new Exception("No connection established");
        this.connection.sendMessage(msg);
    }

    public Message getMessage() throws Exception {
        if(!connectionEstablished)
            throw new Exception("No connection established");
        return this.connection.getMessage();
    }
}
