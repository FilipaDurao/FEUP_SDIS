package proj.client;

import proj.peer.rmi.RemoteBackupInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {

    public static void main(String[] args) {
        if(args.length < 3) {
            System.err.println("Usage:  <peer_p> <operation> <oper_1> <oper_2>");
            System.exit(-1);
        }

        String peer_p = args[0];
        String operation = args[1];

        try {
            Registry reg = LocateRegistry.getRegistry("localhost");
            RemoteBackupInterface remoteBackup = (RemoteBackupInterface) reg.lookup("RBackup" + peer_p);

            if (operation.toUpperCase().equals("BACKUP")) {
                backup(args, remoteBackup);
                System.exit(0);
            }

            System.err.println("Unsupported operation: " + operation);
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void backup(String[] args, RemoteBackupInterface remoteBackup) throws Exception {
        if (args.length < 4) {
            System.err.println("Missing parameters in backup");
            System.exit(-1);
        }

        String pathname = args[2];
        Integer replicationDegree = Integer.valueOf(args[3]);
        Integer res = remoteBackup.backup(pathname, replicationDegree);
        System.out.println("Returned: " + res);
    }
}
