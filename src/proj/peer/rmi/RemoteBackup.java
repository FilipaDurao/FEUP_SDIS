package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.message.Message;
import proj.peer.message.PutChunkMessage;

import java.io.*;


public class RemoteBackup implements  RemoteBackupInterface{

    public static final int CHUNK_SIZE = 64000;
    private String senderId;
    private Peer peer;

    public RemoteBackup(Peer peer) {
        this.senderId = peer.getPeerId();
        this.peer = peer;
    }

    public int backup(String pathname, Integer replicationDegree) throws IOException {
        try
        {
            byte[] buffer = new byte[CHUNK_SIZE];
            FileInputStream in = new FileInputStream(pathname);

            int rc = in.read(buffer);
            for (int i = 0; rc != -1; i++)
            {
                PutChunkMessage msg = new PutChunkMessage(this.senderId, pathname, i, replicationDegree, new String(buffer, 0, buffer.length));
                this.peer.getBackup().sendMessage(msg);
                rc = in.read(buffer);
            }
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", pathname);
            return -1;
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
