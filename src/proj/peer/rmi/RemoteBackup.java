package proj.peer.rmi;

import proj.peer.Peer;


public class RemoteBackup implements  RemoteBackupInterface{

    private final FileRestorer fileRestorer;
    private Peer peer;

    public RemoteBackup(Peer peer) {
        this.peer = peer;
        this.fileRestorer = new FileRestorer(peer);
    }

    public int backup(String pathname, Integer replicationDegree) {
        FileSender fileSender = new FileSender(peer, pathname, replicationDegree);

        if (!fileSender.sendFile()) return -2;
        if (!fileSender.waitOperation()) return -1;

        return 0;
    }

    public int restore(String filename) {
        if (!this.fileRestorer.restoreFile(filename)) return -1;
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
