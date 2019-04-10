package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;
import proj.peer.manager.ChunkInfo;
import proj.peer.manager.FileInfo;
import proj.peer.message.messages.DeleteMessage;
import proj.peer.message.messages.RemovedMessage;
import proj.peer.operations.SendMessageOperation;
import proj.peer.utils.SHA256Encoder;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


public class RemoteBackup implements RemoteBackupInterface {

    private FileRestorer fileRestorer;
    private FileRestorerTCP fileRestorerTCP = null;
    private Peer peer;

    public RemoteBackup(Peer peer) {
        this.peer = peer;

            this.fileRestorer = new FileRestorer(peer);
        if (!this.peer.getVersion().equals(Peer.DEFAULT_VERSION)) {
            this.fileRestorerTCP = new FileRestorerTCP(peer);
        }
    }

    public int backup(String pathname, Integer replicationDegree) {
        FileSender fileSender = new FileSender(peer, pathname, replicationDegree);
        this.peer.getFileManager().addRemoteFile(fileSender.getFileName(), fileSender.getEncodedFileName());

        if (!fileSender.sendFile()) {
            delete(new File(pathname).getName());
            this.peer.getFileManager().removeRemoteFile(fileSender.getEncodedFileName());
            return -2;
        }
        if (!fileSender.waitOperation()) {
            delete(new File(pathname).getName());
            this.peer.getFileManager().removeRemoteFile(fileSender.getEncodedFileName());
            return -1;
        }

        return 0;
    }

    public int restore(String filename) {
        String encoded = SHA256Encoder.encode(this.peer.getPeerId() + "/" + filename);
        if (!this.peer.getFileManager().isFileRemotlyStored(encoded)) return -2;
        if (!this.fileRestorer.restoreFile(filename, encoded)) return -1;

        return 0;
    }

    public int restore_enh(String filename) throws Exception {
        if (this.fileRestorerTCP == null)
            throw  new Exception("TCP version not supported");
        String encoded = SHA256Encoder.encode(this.peer.getPeerId() + "/" + filename);
        if (!this.peer.getFileManager().isFileRemotlyStored(encoded)) return -2;
        if (!this.fileRestorerTCP.restoreFile(filename, encoded)) return -1;

        return 0;
    }


    public int delete(String filename) {
        String encodedFilename = SHA256Encoder.encode(this.peer.getPeerId() + "/"  + filename);
        DeleteMessage deleteMessage = new DeleteMessage(Peer.DEFAULT_VERSION, this.peer.getPeerId(), encodedFilename);
        SendMessageOperation sendMessageOperation = new SendMessageOperation(this.peer.getControl(), deleteMessage);
        this.peer.getScheduler().schedule(sendMessageOperation, 0, TimeUnit.SECONDS);
        this.peer.getScheduler().schedule(sendMessageOperation, 2, TimeUnit.SECONDS);
        this.peer.getScheduler().schedule(sendMessageOperation, 4, TimeUnit.SECONDS);
        this.peer.getFileManager().removeRemoteFile(encodedFilename);
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

        res.append("REMOTE STATE\n");
        for (Map.Entry<String, FileInfo> entry : this.peer.getFileManager().getRemoteChunks().entrySet()) {
            buildFileInfo(res, entry);
        }

        res.append("LOCAL STATE\n");
        res.append("Max Size: ").append(this.peer.getFileManager().getMaxSize() / 1000).append(" KBytes\n");
        res.append("Total size: ").append(this.peer.getFileManager().getFileSize() / 1000).append(" KBytes\n");
        for (Map.Entry<String, FileInfo> entry : this.peer.getFileManager().getChunks().entrySet()) {
            buildFileInfo(res, entry);
        }
        return res.toString();
    }

    private void buildFileInfo(StringBuilder res, Map.Entry<String, FileInfo> entry) {
        res.append("\tFile saved: ").append(entry.getKey()).append("\n\tChunks: \n");
        for (ChunkInfo chunkInfo : entry.getValue().getChunks()) {
            res.append("\t\t").append(chunkInfo.getChunkNumber()).append(":\n");
            res.append("\t\t\tSize: ").append(chunkInfo.getSize()).append("\n");
            res.append("\t\t\tReplication degree: ").append(chunkInfo.getReplicationDegree()).append("\n");
            res.append("\t\t\tPerceived degree: ").append(chunkInfo.getNumberOfSaves()).append("\n");
        }
        res.append("\n");
    }
}
