package proj.peer;

import proj.peer.connection.BackupConnection;
import proj.peer.connection.ControlConnection;
import proj.peer.connection.RestoreConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.manager.FileManager;
import proj.peer.rmi.RemoteBackup;
import proj.peer.rmi.RemoteBackupInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;

public class Peer {

    public static final String DEFAULT_VERSION = "1.0";
    private String version;
    private String peerId;
    private String controlName;
    private Integer controlPort;
    private String backupName;
    private Integer backupPort;
    private String restoreName;
    private Integer restorePort;

    private ScheduledThreadPoolExecutor scheduler;

    private BackupConnection backup;
    private ControlConnection control;

    private FileManager fileManager;
    private RestoreConnection restore;

    public Peer(String version, String peerId, String controlName, Integer controlPort, String backupName, Integer backupPort, String restoreName, Integer restorePort) throws Exception {
        this.version = version;
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
        if (args.length != 8) {
            System.err.println("Usage: java Peer <version> <peer_ap> <control_name> <control_port> <backup_name> <backup_port>  <restore_name> <restore_port>");
            System.exit(-1);
        }

        String version = args[0];
        String peerId = args[1];
        String controlName = args[2];
        Integer controlPort = Integer.valueOf(args[3]);
        String backupName = args[4];
        Integer backupPort = Integer.valueOf(args[5]);
        String restoreName = args[6];
        Integer restorePort = Integer.valueOf(args[7]);

        System.out.println(version);
        Peer peer = new Peer(version, peerId, controlName, controlPort, backupName, backupPort, restoreName, restorePort);
        peer.establishRMI();
        peer.startConnections();

        Runtime.getRuntime().addShutdownHook(new Thread(peer.fileManager));

        NetworkLogger.printLog(Level.INFO, "Server is running.");
    }

    private void startConnections() throws IOException {
        this.scheduler = new ScheduledThreadPoolExecutor(3);

        this.backup = new BackupConnection(this, backupName, backupPort);
        new Thread(this.backup).start();

        this.control = new ControlConnection(this, controlName, controlPort);
        new Thread(this.control).start();

        this.restore = new RestoreConnection(this, restoreName, restorePort);
        new Thread(this.restore).start();
    }

    private void establishRMI() throws IOException {
        RemoteBackup reBackup = new RemoteBackup(this);
        RemoteBackupInterface stub;
        try {
            stub = (RemoteBackupInterface) UnicastRemoteObject.exportObject(reBackup, 0);
            Registry reg;
            try {
                reg = LocateRegistry.getRegistry();
                reg.rebind("RBackup" + this.peerId, stub);
            } catch (RemoteException e) {
                NetworkLogger.printLog(Level.INFO, "Tries to create Registry");
                reg = LocateRegistry.createRegistry(1099);
                reg.rebind("RBackup" + this.peerId, stub);
            }

            NetworkLogger.printLog(Level.INFO, "RMI established");
        } catch (RemoteException e) {
            NetworkLogger.printLog(Level.SEVERE, "Failed establishing RMI - " + e.getMessage());
        }
    }

    public String getPeerId() {
        return peerId;
    }

    public BackupConnection getBackup() {
        return backup;
    }

    public ControlConnection getControl() {
        return control;
    }

    public RestoreConnection getRestore() {
        return restore;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ScheduledThreadPoolExecutor getScheduler() {
        return scheduler;
    }

    public String getVersion() {
        return version;
    }
}
