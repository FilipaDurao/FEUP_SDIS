package proj.peer;

import proj.peer.connection.ControlConnection;
import proj.peer.connection.DataBackup;
import proj.peer.connection.RunnableMC;
import proj.peer.manager.FileManager;
import proj.peer.rmi.RemoteBackup;
import proj.peer.rmi.RemoteBackupInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Peer {

    private String peerId;
    private String controlName;
    private Integer controlPort;
    private String backupName;
    private Integer backupPort;
    private String restoreName;
    private Integer restorePort;

    private LinkedBlockingQueue<Runnable> runQueue;
    private ThreadPoolExecutor executor;

    private DataBackup backup;
    private ControlConnection control;


    private FileManager fileManager;

    public Peer(String peerId, String controlName, Integer controlPort, String backupName, Integer backupPort, String restoreName, Integer restorePort) throws Exception {
        this.peerId = peerId;
        this.controlName = controlName;
        this.controlPort = controlPort;
        this.backupName = backupName;
        this.backupPort = backupPort;
        this.restoreName = restoreName;
        this.restorePort = restorePort;

        this.fileManager = new FileManager(this.peerId);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 7) {
            System.err.println("Usage: java Peer <peer_ap>");
            System.exit(-1);
        }

        String peerId = args[0];
        String controlName = args[1];
        Integer controlPort = Integer.valueOf(args[2]);
        String backupName = args[3];
        Integer backupPort = Integer.valueOf(args[4]);
        String restoreName = args[5];
        Integer restorePort = Integer.valueOf(args[6]);

        Peer peer = new Peer(peerId, controlName, controlPort, backupName, backupPort, restoreName, restorePort);
        peer.establishRMI();
        peer.startConnections();
        System.out.println("Server Ready");
    }

    private void startConnections() throws IOException {
        this.runQueue = new LinkedBlockingQueue<Runnable>();
        this.executor = new ThreadPoolExecutor(3, 5, 1, TimeUnit.SECONDS, this.runQueue);

        this.backup = new DataBackup(this, backupName, backupPort);
        this.executor.execute(this.backup);

        this.control = new ControlConnection(controlName, controlPort);
    }

    private void establishRMI() {
        RemoteBackup reBackup = new RemoteBackup(this);
        RemoteBackupInterface stub;
        try {
            stub = (RemoteBackupInterface) UnicastRemoteObject.exportObject(reBackup, 0);
            Registry reg;
            try {
                reg = LocateRegistry.getRegistry();
                reg.rebind("RBackup" + this.peerId, stub);
            } catch (RemoteException e) {
                System.out.println("Tries to create");
                reg = LocateRegistry.createRegistry(1099);
                reg.rebind("RBackup" + this.peerId, stub);
            }

            System.out.println("Created RBackup" + this.peerId);

        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
    }

    public String getPeerId() {
        return peerId;
    }

    public DataBackup getBackup() {
        return backup;
    }

    public ControlConnection getControl() {
        return control;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
