package proj.peer.rmi;

import proj.peer.Peer;


public class RemoteBackup implements  RemoteBackupInterface{

    private Peer peer;

    public RemoteBackup(Peer peer) {

        this.peer = peer;
    }

    public int backup(String pathname, Integer replicationDegree) {
        FileSaver fileSaver = new FileSaver(peer);

        if (!fileSaver.sendFile(pathname, replicationDegree) || !fileSaver.waitOperation()) return -1;

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
