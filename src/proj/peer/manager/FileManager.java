package proj.peer.manager;

import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.RemovedMessage;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class FileManager implements Runnable {

    public static final String FILENAME = "localManager.ser";
    private FileStructure fileStructure;
    private String peerId;

    public FileManager(String peerId) throws Exception {
        this.peerId = peerId;
        this.recoverFileStructure();
    }

    private String getFilename() {
        return "data/peer_" + peerId + "/" + FILENAME;
    }

    private void saveFileStructure() {
        try {
            FileOutputStream fos = new FileOutputStream(this.getFilename());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.fileStructure);
            oos.close();

        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Failed saving of the file structure.");
        }

    }


    private void recoverFileStructure() throws Exception {
        try {
            FileInputStream fos = new FileInputStream(this.getFilename());
            ObjectInputStream oos = new ObjectInputStream(fos);
            Object saved = oos.readObject();
            if (saved instanceof FileStructure) {
                this.fileStructure = (FileStructure) saved;
            } else {
                throw new Exception("Wrong class saved.");

            }
            oos.close();
            this.fileStructure.checkFileStructure();
        } catch (Exception e) {
            this.fileStructure = new FileStructure("data/peer_" + peerId + "/backup");
            NetworkLogger.printLog(Level.WARNING, "Failed recovering of the file structure.");
        }
    }


    public void putChunk(String fileId, Integer chunkId, byte[] content, Integer replicationDegree) throws Exception {
        fileStructure.putChunk(fileId, chunkId, content, replicationDegree);
    }

    public void storeChunkPeer(String fileId, Integer chunkId, String peerId) {
        fileStructure.storeChunkPeer(fileId, chunkId, peerId);
    }


    public byte[] getChunk(String fileId, Integer chunkId) throws Exception {

        return fileStructure.getChunk(fileId, chunkId);
    }

    public RemovedMessage deleteChunk() throws Exception {
        String fileId = null;
        Integer chunkId = null;
        for (Map.Entry<String, FileInfo> entry : this.fileStructure.getSavedFiles().entrySet()) {
            for (ChunkInfo chunkInfo : entry.getValue().getChunks()) {
                if (chunkInfo.getReplicationDegree() < chunkInfo.getNumberOfSaves()) {
                    fileId = entry.getKey();
                    chunkId = chunkInfo.getChunkNumber();
                    fileStructure.deleteChunk(fileId, chunkId);
                    NetworkLogger.printLog(Level.INFO, "Deleted chunk - " + fileId.substring(0, 5) + " - " + chunkId);
                    return null;
                }

                if (fileId == null || chunkId == null) {
                    fileId = entry.getKey();
                    chunkId = chunkInfo.getChunkNumber();
                }
            }
        }

        if (fileId == null || chunkId == null) {
            throw new Exception("No chunks stored");
        }
        
        fileStructure.deleteChunk(fileId, chunkId);
        NetworkLogger.printLog(Level.INFO, "Deleted file - " + fileId.substring(0, 5) + " - " + chunkId);
        return new RemovedMessage(peerId, fileId, chunkId);
    }

    public void deleteFile(String fileId) throws Exception {

        fileStructure.deleteFile(fileId);
    }

    public boolean isChunkSaved(String fileId, Integer chunkId) {
        return fileStructure.isChunkSaved(fileId, chunkId);
    }

    public boolean isFileSaved(String fileId) {
        return fileStructure.isFileSaved(fileId);
    }

    public int getFileSize() {
        return fileStructure.getSavedSize();
    }

    public ConcurrentHashMap<String, FileInfo> getChunks() {
        return this.fileStructure.getSavedFiles();
    }

    public void setMaxSize(Integer maxSize) {
        this.fileStructure.setMaxSize(maxSize);
    }

    public boolean isPeerOversized() {
        return this.fileStructure.getMaxSize() < this.fileStructure.getSavedSize();
    }

    @Override
    public void run() {
        this.saveFileStructure();
    }
}
