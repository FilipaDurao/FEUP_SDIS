package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.manager.ChunkInfo;
import proj.peer.manager.FileInfo;
import proj.peer.message.messages.DeleteMessage;
import proj.peer.message.messages.RemovedMessage;
import proj.peer.operations.SendMessageOperation;
import proj.peer.utils.SHA256Encoder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


public class RemoteBackup implements RemoteBackupInterface {

    private final FileRestorer fileRestorer;
    private Peer peer;

    public RemoteBackup(Peer peer) throws IOException {
        this.peer = peer;
        if (this.peer.getVersion().equals(Peer.DEFAULT_VERSION))
            this.fileRestorer = new FileRestorer(peer);
        else
            this.fileRestorer = new FileRestorerTCP(peer);
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
        try {
            this.peer.getFileManager().setMaxSize(diskSpace * 1000);
            while (this.peer.getFileManager().isPeerOversized()) {
                // Remove a chunk
                RemovedMessage removedMessage = this.peer.getFileManager().deleteChunk();
                if (removedMessage != null) {
                    SendMessageOperation sendMessageOperation = new SendMessageOperation(this.peer.getControl(), removedMessage);
                    this.peer.getScheduler().schedule(sendMessageOperation, 0, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            NetworkLogger.printLog(Level.SEVERE, "Failed space reclaiming - " + e.getMessage());
            return -1;
        }
        return 0;
    }

    public String state() {
        StringBuilder res = new StringBuilder();
        res.append("Total size: ").append(this.peer.getFileManager().getFileSize()).append(" bytes\n");
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
