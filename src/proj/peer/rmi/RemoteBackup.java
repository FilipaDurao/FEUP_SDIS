package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.message.messages.GetChunkMessage;
import proj.peer.utils.SHA256Encoder;

import java.io.IOException;


public class RemoteBackup implements  RemoteBackupInterface{

    private Peer peer;

    public RemoteBackup(Peer peer) {

        this.peer = peer;
    }

    public int backup(String pathname, Integer replicationDegree) {
        FileSender fileSender = new FileSender(peer, pathname, replicationDegree);

        if (!fileSender.sendFile()) return -2;
        if (!fileSender.waitOperation()) return -1;

        return 0;
    }

    public int restore(String filename) {
        try {
            System.out.println("Restore: " + filename);
            GetChunkMessage msg = new GetChunkMessage(this.peer.getVersion(), this.peer.getPeerId(), SHA256Encoder.encode(filename), 0);
            this.peer.getControl().sendMessage(msg);
        } catch (IOException e) {
            return -1;
        }
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
