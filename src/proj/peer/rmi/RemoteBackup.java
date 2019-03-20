package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.message.Message;
import proj.peer.message.PutChunkMessage;

import java.io.IOException;


public class RemoteBackup implements  RemoteBackupInterface{

    private String senderId;
    private Peer peer;

    public RemoteBackup(Peer peer) {
        this.senderId = peer.getPeerId();
        this.peer = peer;
    }

    public int backup(String pathname, Integer replicationDegree)  {
        Message msg = new PutChunkMessage(this.senderId, pathname,0, replicationDegree, "this is the body");
        try {
            this.peer.getBackup().sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
