package proj.peer;

import proj.peer.connection.BackupConnection;
import proj.peer.connection.ControlConnection;
import proj.peer.connection.RestoreConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.manager.FileManager;
import proj.peer.operations.SaveStructureOperation;
import proj.peer.rmi.RemoteBackup;
import proj.peer.rmi.RemoteBackupInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Peer {

    public static final String DEFAULT_VERSION = "1.0";
    public static final int POOL_SIZE = 7;
    private String version;
    private String peerId;
    private String peer_ap;
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

    public Peer(String version, String peerId, String peer_ap, String controlName, Integer controlPort, String backupName, Integer backupPort, String restoreName, Integer restorePort) throws Exception {
        this.version = version;
        this.peerId = peerId;
        this.peer_ap = peer_ap;
        this.controlName = controlName;
        this.controlPort = controlPort;
        this.backupName = backupName;
        this.backupPort = backupPort;
        this.restoreName = restoreName;
        this.restorePort = restorePort;

        this.fileManager = new FileManager(this.peerId);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 9) {
            System.err.println("Usage: java Peer <version> <peer_id> <peer_ap> <control_name> <control_port> <backup_name> <backup_port>  <restore_name> <restore_port>");
            System.exit(-1);
        }

        String version = args[0];
        String peerId = args[1];
        String peer_ap = args[2];
        String controlName = args[3];
        Integer controlPort = Integer.valueOf(args[4]);
        String backupName = args[5];
        Integer backupPort = Integer.valueOf(args[6]);
        String restoreName = args[7];
        Integer restorePort = Integer.valueOf(args[8]);
        NetworkLogger.setPeerId(peerId);

        Peer peer = new Peer(version, peerId, peer_ap, controlName, controlPort, backupName, backupPort, restoreName, restorePort);
        peer.establishRMI();
        peer.startConnections();

        Runtime.getRuntime().addShutdownHook(new Thread(new SaveStructureOperation(peer.fileManager, true)));
        NetworkLogger.printLog(Level.INFO, "Server is running.");
    }

    private void startConnections() throws IOException {
        this.scheduler = new ScheduledThreadPoolExecutor(POOL_SIZE);
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.scheduler.scheduleAtFixedRate(new SaveStructureOperation(this.fileManager, false), 0, 30, TimeUnit.SECONDS);

        this.backup = new BackupConnection(this, backupName, backupPort);
        new Thread(this.backup).start();

        this.control = new ControlConnection(this, controlName, controlPort);
        new Thread(this.control).start();

        this.restore = new RestoreConnection(this, restoreName, restorePort);
        new Thread(this.restore).start();
    }

    private void establishRMI() {
        RemoteBackup reBackup = new RemoteBackup(this);
        RemoteBackupInterface stub;
        try {
            stub = (RemoteBackupInterface) UnicastRemoteObject.exportObject(reBackup, 0);
            Registry reg;
            try {
                reg = LocateRegistry.getRegistry();
                reg.rebind(this.peer_ap, stub);
            } catch (RemoteException e) {
                NetworkLogger.printLog(Level.INFO, "Tries to create Registry");
                reg = LocateRegistry.createRegistry(1099);
                reg.rebind(this.peer_ap, stub);
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
