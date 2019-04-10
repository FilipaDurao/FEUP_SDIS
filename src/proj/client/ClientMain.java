package proj.client;

import proj.peer.rmi.RemoteBackupInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("Usage:  <peer_p> <operation> <oper_1> <oper_2>");
            System.exit(-1);
        }

        String peer_p = args[0];
        String operation = args[1];

        try {
            Registry reg = LocateRegistry.getRegistry("localhost");
            RemoteBackupInterface remoteBackup = (RemoteBackupInterface) reg.lookup(peer_p);

            if (operation.toUpperCase().equals("BACKUP")) {
                backup(args, remoteBackup);
                System.exit(0);
            } else if (operation.toUpperCase().equals("RESTORE")) {
                restore(args, remoteBackup);
                System.exit(0);
            } else if (operation.toUpperCase().equals("DELETE")) {
                delete(args, remoteBackup);
                System.exit(0);
            } else if (operation.toUpperCase().equals("RECLAIM")) {
                reclaim(args, remoteBackup);
                System.exit(0);
            } else if (operation.toUpperCase().equals("STATE")) {
                state(args, remoteBackup);
                System.exit(0);
            }



            System.err.println("Unsupported operation: " + operation);
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void state(String[] args, RemoteBackupInterface remoteBackup) throws RemoteException {
        if (args.length < 2) {
            System.err.println("Missing parameters in state");
            System.exit(-1);
        }

        String res = remoteBackup.state();
        System.out.println(res);

    }

    private static void reclaim(String[] args, RemoteBackupInterface remoteBackup) throws RemoteException {
        if (args.length < 3) {
            System.err.println("Missing parameters in reclaim");
            System.exit(-1);
        }

        int diskSpace = Integer.valueOf(args[2]);
        remoteBackup.reclaim(diskSpace);
    }

    private static void delete(String[] args, RemoteBackupInterface remoteBackup) throws RemoteException {
       if (args.length < 3) {
            System.err.println("Missing parameters in delete");
            System.exit(-1);
        }
        String filename = args[2];

        int res = remoteBackup.delete(filename);

        System.out.println("Returned: " + res);
        if (res != 0) {
            System.out.println("Delete failed");
        } else {
            System.out.println("Succeeded");
        }
    }

    private static void restore(String[] args, RemoteBackupInterface remoteBackup) throws RemoteException {
        if (args.length < 3) {
            System.err.println("Missing parameters in restore");
            System.exit(-1);
        }
        String filename = args[2];

        int res = remoteBackup.restore(filename);

        System.out.println("Returned: " + res);
        if (res != 0) {
            System.out.println("Restore failed");
        } else {
            System.out.println("Succeeded");
        }

    }

    private static void backup(String[] args, RemoteBackupInterface remoteBackup) throws Exception {
        if (args.length < 4) {
            System.err.println("Missing parameters in backup");
            System.exit(-1);
        }

        String pathname = args[2];
        Integer replicationDegree = Integer.valueOf(args[3]);
        int res = remoteBackup.backup(pathname, replicationDegree);

        System.out.println("Returned: " + res);
        if (res != 0) {
            System.out.println("Backup failed");
        } else {
            System.out.println("Succeeded");
        }
    }
}
