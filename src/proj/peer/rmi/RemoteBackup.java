package proj.peer.rmi;

import proj.peer.Peer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class RemoteBackup implements  RemoteBackupInterface{

    public static final int CHUNK_SIZE = 64000;
    private ChunkSender chunkSender;

    public RemoteBackup(Peer peer) {
        this.chunkSender = new ChunkSender(peer);
    }

    public int backup(String pathname, Integer replicationDegree) {
        File file = new File(pathname);
        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[CHUNK_SIZE];
            double nChunks = data.length() / (double) CHUNK_SIZE;
            int i = 0;
            for (i = 0; i < nChunks; i++) {
                data.read(buffer);
                chunkSender.sendChunk(replicationDegree, file, new String(buffer, 0, buffer.length), i);
            }

            if (nChunks == Math.floor(nChunks)) {
                chunkSender.sendChunk(replicationDegree, file, "", i);
            }
        } catch (IOException e) {
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
