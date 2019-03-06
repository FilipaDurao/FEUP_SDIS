package proj.peer.rmi;

import java.rmi.RemoteException;

public class RemoteBackup implements  RemoteBackupInterface{
    public RemoteBackup() {}

    public int backup(String pathname, Integer replicationDegree) {
        System.out.println("Backup: ");
        System.out.println(pathname);
        System.out.println(replicationDegree);
        return 0;
    }

    public int restore(String pathname) {
        System.out.println("Restore: " + pathname);
        return 0;
    }

    public int delete(String pathname) {
        System.out.println("Delete: " + pathname);
        return 0;
    }

    public int reclaim(Integer diskSpace) {
        System.out.println("Reclaim: " + diskSpace);
        return 0;
    }

    public int state() {
        System.out.println("STATE");
        return 0;
    }
}
