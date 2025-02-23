package proj.peer.manager;

import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.RemovedMessage;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class FileManager {

    public static final String FILENAME = "localManager.ser";
    private FileStructure fileStructure;
    private boolean structureChanged;
    private String peerId;

    public FileManager(String peerId) throws Exception {
        this.peerId = peerId;
        this.structureChanged = false;
        this.recoverFileStructure();
    }

    private String getFilename() {
        return "peer_" + peerId + "/" + FILENAME;
    }

    public void saveFileStructure() {
        try {
            FileOutputStream fos = new FileOutputStream(this.getFilename());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.fileStructure);
            this.structureChanged = false;
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
            this.fileStructure = new FileStructure("peer_" + peerId + "/backup");
            NetworkLogger.printLog(Level.WARNING, "Failed recovering of the file structure.");
        }
    }


    public void putChunk(String fileId, Integer chunkId, byte[] content, Integer replicationDegree) throws Exception {
        fileStructure.putChunk(fileId, chunkId, content, replicationDegree);
        this.structureChanged = true;
    }

    public void storeChunkPeer(String fileId, Integer chunkId, String peerId) {
        fileStructure.storeChunkPeer(fileId, chunkId, peerId);
        this.structureChanged = true;
    }

    public void removeChunkPeer(String fileId, Integer chunkId, String peerId) {
        fileStructure.removeChunkPeer(fileId, chunkId, peerId);
        this.structureChanged = true;

    }

    public byte[] getChunk(String fileId, Integer chunkId) throws Exception {
        return fileStructure.getChunk(fileId, chunkId);
    }

    public RemovedMessage deleteChunk() throws Exception {
        String fileId = null;
        Integer chunkId = null;
        for (Map.Entry<String, FileInfo> entry : this.fileStructure.getLocalFiles().entrySet()) {
            for (ChunkInfo chunkInfo : entry.getValue().getChunks()) {
                if (chunkInfo.getReplicationDegree() < chunkInfo.getNumberOfSaves()) {
                    fileId = entry.getKey();
                    chunkId = chunkInfo.getChunkNumber();
                    this.structureChanged = true;
                    return deleteChunkFromMemory(fileId, chunkId);
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

        this.structureChanged = true;
        return deleteChunkFromMemory(fileId, chunkId);
    }

    private RemovedMessage deleteChunkFromMemory(String fileId, Integer chunkId) throws Exception {
        this.structureChanged = true;
        fileStructure.deleteChunk(fileId, chunkId);
        NetworkLogger.printLog(Level.INFO, "Deleted chunk - " + fileId.substring(0, 5) + " - " + chunkId);
        return new RemovedMessage(peerId, fileId, chunkId);
    }

    public void deleteFile(String fileId) throws Exception {
        fileStructure.deleteFile(fileId);
        this.structureChanged = true;
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

    public int getMaxSize() {
        return fileStructure.getMaxSize();
    }

    public ConcurrentHashMap<String, FileInfo> getChunks() {
        return this.fileStructure.getLocalFiles();
    }

    public ConcurrentHashMap<String, FileInfo> getRemoteChunks() {
        return this.fileStructure.getRemoteFiles();
    }

    public void setMaxSize(Integer maxSize) {
        this.fileStructure.setMaxSize(maxSize);
    }

    public boolean isPeerOversized() {
        return this.fileStructure.getMaxSize() < this.fileStructure.getSavedSize();
    }

    public boolean hasSpace(Integer size) {return this.fileStructure.getMaxSize() > this.fileStructure.getSavedSize() + size;}

    public void addRemoteFile(String filename, String encoded) {
        this.fileStructure.addRemoteFile(filename, encoded);
    }

    public void removeRemoteFile(String fileId) {
        this.structureChanged = true;
        this.fileStructure.removeRemoteFile(fileId);
    }

    public ChunkInfo getChunkInfo(String fileId, Integer chunkNo) throws Exception {
        return this.fileStructure.getChunkInfo(fileId, chunkNo);
    }

    public void addRemoteChunk(String fileId, Integer chunkId, Integer replication, Integer size) {
        this.fileStructure.addRemoteChunk(fileId, chunkId, replication, size);
        this.structureChanged = true;
    }

    public boolean isFileRemotlyStored(String fileId) {
        return this.fileStructure.isFileRemotelyStored(fileId);
    }

    public int getRemoteNChunks(String fileId) throws Exception {
        this.structureChanged = true;
        return this.fileStructure.getRemoteNChunks(fileId);
    }

    public void setChunkSize(String fileId, Integer chunkNo, int length) {
        this.structureChanged = true;
        this.fileStructure.setChunkSize(fileId, chunkNo, length);
    }

    public boolean isStructureChanged() {
        return structureChanged;
    }
}
