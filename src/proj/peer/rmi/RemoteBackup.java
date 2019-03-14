package proj.peer.rmi;

import proj.peer.message.Message;
import proj.peer.message.PutChunkMessage;


public class RemoteBackup implements  RemoteBackupInterface{

    private String senderId;

    public RemoteBackup(String senderId) {
        this.senderId = senderId;
    }

    public int backup(String pathname, Integer replicationDegree) throws Exception {
        Message msg = new PutChunkMessage(this.senderId, pathname,0, replicationDegree, "this is the body");
        Message convMsg =  new PutChunkMessage(msg.toString());
        System.out.println(convMsg);
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
