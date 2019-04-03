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
    private ConcurrentHashMap<String, FileInfo> savedFiles;
    private File rootFolder;
    private Integer savedSize;
    private Integer maxSize;

    public FileStructure(String rootFolderPath) throws Exception {
        this.savedFiles =  new ConcurrentHashMap<>();
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

        if (this.savedFiles.containsKey(fileId)) {
            FileInfo chunks = this.savedFiles.get(fileId);

            if (chunks.contains(chunkId)) {
                this.savedSize -= chunks.getSize(chunkId);
            }
            chunks.addChunk(chunkId, replicationDegree, content.length);

        } else {
            FileInfo info = new FileInfo();
            info.addChunk(chunkId, replicationDegree, content.length);
            this.savedFiles.put(fileId, info);
        }
        this.savedSize += content.length;
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
        if (file.delete()) {
            int size = this.savedFiles.get(fileId).deleteChunk(chunkId);
            this.savedSize -= size;
            return;
        }
        throw new Exception("Failed chunk deletion");

    }

    public void deleteFile(String fileId) throws Exception {
        if (!this.savedFiles.containsKey(fileId)) {
            throw new Exception("File not found");
        }

        File folder = new File(this.rootFolder.getAbsolutePath() + "/" + fileId);
        if (deleteFolderFromMemory(folder)) {
            this.savedSize -= this.savedFiles.get(fileId).getSize();
            this.savedFiles.remove(fileId);
            return;
        }

        throw new Exception("Failed file deletion");

    }

    private boolean deleteFolderFromMemory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteFolderFromMemory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return directory.delete();
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

    public Integer getSavedSize() {
        return savedSize;
    }

    public ConcurrentHashMap<String, FileInfo> getSavedFiles() {
        return savedFiles;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
}