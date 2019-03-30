package proj.peer.manager;

import proj.peer.log.NetworkLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class FileStructure implements Serializable {
    private ConcurrentHashMap<String, FileInfo> savedFiles;
    private File rootFolder;

    public FileStructure(String rootFolderPath) throws Exception {
        this.savedFiles =  new ConcurrentHashMap<>();

        this.rootFolder = new File(rootFolderPath);

        if (!this.rootFolder.mkdirs() && !this.rootFolder.isDirectory()) {
            throw new Exception("Root folder is not a directory.");
        }

    }


    public void putChunk(String fileId, Integer chunkId, byte[] content, Integer replicationDegree) throws IOException {
        File fileFolder = new File(this.rootFolder.getAbsolutePath() + "/" + fileId);
        fileFolder.mkdirs();

        try (FileOutputStream stream = new FileOutputStream(fileFolder.getAbsolutePath() + "/" + chunkId)) {
            stream.write(content);
        }

        if (this.savedFiles.containsKey(fileId)) {
            FileInfo chunks = this.savedFiles.get(fileId);
            chunks.addChunk(chunkId, replicationDegree);
        } else {
            FileInfo info = new FileInfo();
            info.addChunk(chunkId, replicationDegree);
            this.savedFiles.put(fileId, info);
        }

    }

    public void storeChunkPeer(String fileId, Integer chunkId, String peerId) {
        if (this.savedFiles.containsKey(fileId)) {
            this.savedFiles.get(fileId).addPeerId(chunkId, peerId);
        }
    }

    public byte[] getChunk(String fileId, Integer chunkId) throws Exception {
        if (!this.savedFiles.containsKey(fileId) || !this.savedFiles.get(fileId).contains(chunkId)) {
            throw new Exception("File not found");
        }

        NetworkLogger.printLog(Level.INFO, "Getting file from memory");
        return Files.readAllBytes(Paths.get(this.rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkId));
    }

    public void deleteChunk(String fileId, Integer chunkId) throws Exception {
        if (!this.savedFiles.containsKey(fileId) || !this.savedFiles.get(fileId).contains(chunkId)) {
            throw new Exception("File not found");
        }

        File file = new File(this.rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkId);
        file.delete();
    }

    public void deleteFile(String fileId) throws Exception {
        if (!this.savedFiles.containsKey(fileId)) {
            throw new Exception("File not found");
        }

        FileInfo chunks = this.savedFiles.get(fileId);
        for (ChunkInfo chunk : chunks.getChunks()) {
            this.deleteChunk(fileId, chunk.getChunkNumber());
        }

    }

    public boolean isChunkSaved(String fileId, Integer chunkId) {
        return this.isFileSaved(fileId) && this.savedFiles.get(fileId).contains(chunkId);
    }

    public boolean isFileSaved(String fileId) {
        return this.savedFiles.containsKey(fileId);
    }

    public void checkFileStructure() {
        for (Map.Entry<String, FileInfo> entry : this.savedFiles.entrySet()) {
            String fileId = entry.getKey();
            for (ChunkInfo chunkInfo :  entry.getValue().getChunks()) {
                String filename = this.rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkInfo.getChunkNumber();
                File f = new File(filename);
                if (!f.exists()) {
                    NetworkLogger.printLog(Level.SEVERE, "Missing File - " + filename);
                }
            }
        }
    }
}