package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.manager.ChunkInfo;
import proj.peer.manager.FileInfo;
import proj.peer.message.messages.DeleteMessage;
import proj.peer.operations.SendMessageOperation;
import proj.peer.utils.SHA256Encoder;

import java.util.Map;
import java.util.concurrent.TimeUnit;


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


    public int delete(String filename) {
        String encodedFilename = SHA256Encoder.encode(filename);
        DeleteMessage deleteMessage = new DeleteMessage(Peer.DEFAULT_VERSION, this.peer.getPeerId(), encodedFilename);
        SendMessageOperation sendMessageOperation = new SendMessageOperation(this.peer.getControl(), deleteMessage);
        this.peer.getScheduler().schedule(sendMessageOperation, 0, TimeUnit.SECONDS);
        this.peer.getScheduler().schedule(sendMessageOperation, 2, TimeUnit.SECONDS);
        this.peer.getScheduler().schedule(sendMessageOperation, 4, TimeUnit.SECONDS);
        return 0;
    }

    public int reclaim(Integer diskSpace) {
        System.out.println("Reclaim: " + diskSpace);
        return 0;
    }

    public String state() {
        StringBuilder res = new StringBuilder();
        res.append("Total size: ").append(this.peer.getFileManager().getFileSize()).append("\n");
        for (Map.Entry<String, FileInfo> entry : this.peer.getFileManager().getChunks().entrySet()) {
            res.append("\t File saved: ").append(entry.getKey()).append("\n\t\tChunks: ");
            for (ChunkInfo chunkInfo : entry.getValue().getChunks()) {
                res.append(chunkInfo.getChunkNumber()).append(" ");
            }
            res.append("\n");
        }
        return res.toString();
    }
}
