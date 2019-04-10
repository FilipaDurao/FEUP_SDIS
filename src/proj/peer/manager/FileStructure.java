package proj.peer.manager;

import proj.peer.log.NetworkLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class FileStructure implements Serializable {
    public static final Integer DEFAULT_MAX_SIZE = 128000000;
    private ConcurrentHashMap<String, FileInfo> localFiles;
    private ConcurrentHashMap<String, FileInfo> remoteFiles;
    private File rootFolder;
    private Integer savedSize;
    private Integer maxSize;

    public FileStructure(String rootFolderPath) throws Exception {
        this.localFiles = new ConcurrentHashMap<>();
        this.remoteFiles = new ConcurrentHashMap<>();
        this.rootFolder = new File(rootFolderPath);
        this.savedSize = 0;
        this.maxSize = DEFAULT_MAX_SIZE;

        if (!this.rootFolder.mkdirs() && !this.rootFolder.isDirectory()) {
            throw new Exception("Root folder is not a directory.");
        }

    }


    public void putChunk(String fileId, Integer chunkId, byte[] content, Integer replicationDegree) throws Exception {
        if (content.length + this.savedSize > this.maxSize) {
            throw new Exception("Not enough space to store");
        }

        File fileFolder = new File(this.rootFolder.getAbsolutePath() + "/" + fileId);
        fileFolder.mkdirs();

        try (FileOutputStream stream = new FileOutputStream(fileFolder.getAbsolutePath() + "/" + chunkId)) {
            stream.write(content);
        }

        if (this.localFiles.containsKey(fileId)) {
            FileInfo chunks = this.localFiles.get(fileId);

            if (chunks.contains(chunkId)) {
                this.savedSize -= chunks.getSize(chunkId);
            }
            chunks.addChunk(chunkId, replicationDegree, content.length);

        } else {
            FileInfo info = new FileInfo(fileId);
            info.addChunk(chunkId, replicationDegree, content.length);
            this.localFiles.put(fileId, info);
        }
        this.savedSize += content.length;
    }

    public void storeChunkPeer(String fileId, Integer chunkId, String peerId) {
        if (this.localFiles.containsKey(fileId)) {
            this.localFiles.get(fileId).addPeerId(chunkId, peerId);
        }

        if (this.remoteFiles.containsKey(fileId)) {
            this.remoteFiles.get(fileId).addPeerId(chunkId, peerId);
        }
    }

    public void addRemoteFile(String filename, String encoded) {
        if (!this.remoteFiles.containsKey(encoded)) {
            this.remoteFiles.put(encoded, new FileInfo(filename));
        }
    }

    public void addRemoteChunk(String fileId, Integer chunkId, Integer replication, Integer size) {
        if (this.remoteFiles.containsKey(fileId)) {
            FileInfo chunks = this.remoteFiles.get(fileId);

            chunks.addChunk(chunkId, replication, size);

        }
    }

    public void removeRemoteFile(String fileId) {
        this.remoteFiles.remove(fileId);
    }

    public byte[] getChunk(String fileId, Integer chunkId) throws Exception {
        if (!this.localFiles.containsKey(fileId) || !this.localFiles.get(fileId).contains(chunkId)) {
            throw new Exception("File not found");
        }

        NetworkLogger.printLog(Level.INFO, "Getting file from memory");
        return Files.readAllBytes(Paths.get(this.rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkId));
    }

    public void deleteChunk(String fileId, Integer chunkId) throws Exception {
        if (!this.localFiles.containsKey(fileId) || !this.localFiles.get(fileId).contains(chunkId)) {
            throw new Exception("File not found");
        }

        File file = new File(this.rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkId);
        if (file.delete()) {
            int size = this.localFiles.get(fileId).deleteChunk(chunkId);
            this.savedSize -= size;
            return;
        }
        throw new Exception("Failed chunk deletion");

    }

    public void deleteFile(String fileId) throws Exception {
        if (!this.localFiles.containsKey(fileId)) {
            throw new Exception("File not found");
        }

        File folder = new File(this.rootFolder.getAbsolutePath() + "/" + fileId);
        if (deleteFolderFromMemory(folder)) {
            this.savedSize -= this.localFiles.get(fileId).getSize();
            this.localFiles.remove(fileId);
            return;
        }

        throw new Exception("Failed file deletion");

    }

    private boolean deleteFolderFromMemory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteFolderFromMemory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    public boolean isChunkSaved(String fileId, Integer chunkId) {
        return this.isFileSaved(fileId) && this.localFiles.get(fileId).contains(chunkId);
    }

    public boolean isFileSaved(String fileId) {
        return this.localFiles.containsKey(fileId);
    }

    public void checkFileStructure() {
        for (Map.Entry<String, FileInfo> entry : this.localFiles.entrySet()) {
            String fileId = entry.getKey();
            for (ChunkInfo chunkInfo : entry.getValue().getChunks()) {
                String filename = this.rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkInfo.getChunkNumber();
                File f = new File(filename);
                if (!f.exists()) {
                    NetworkLogger.printLog(Level.SEVERE, "Missing File - " + filename);
                }
            }
        }
    }

    public Integer getSavedSize() {
        return savedSize;
    }

    public ConcurrentHashMap<String, FileInfo> getLocalFiles() {
        return localFiles;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public ChunkInfo getChunkInfo(String fileId, Integer chunkNo) throws Exception {
        if(isChunkSaved(fileId, chunkNo)) {
            return  this.localFiles.get(fileId).getChunkInfo(chunkNo);
        }

        throw new Exception("Chunk not found");
    }

    public void removeChunkPeer(String fileId, Integer chunkId, String peerId) {
        if (this.localFiles.containsKey(fileId)) {
            this.localFiles.get(fileId).removePeerId(chunkId, peerId);
        }
    }

    public ConcurrentHashMap<String, FileInfo> getRemoteFiles() {
        return remoteFiles;
    }

    public boolean isFileRemotlyStored(String fileId) {
        return this.remoteFiles.containsKey(fileId);
    }

    public int getRemoteNChunks(String fileId) throws Exception {
        if (!isFileRemotlyStored(fileId))
            throw new Exception("File not found");

        return this.remoteFiles.get(fileId).getChunks().size();

    }
}