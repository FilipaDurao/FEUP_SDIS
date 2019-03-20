package proj.peer.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileManager {

    private File rootFolder;
    private HashMap<String, FileInfo> savedFiles;

    public FileManager(String peerId) throws Exception {
        this.savedFiles = new HashMap<>();
        this.rootFolder = new File("data/backup_" + peerId);

        if (!this.rootFolder.mkdirs() && !this.rootFolder.isDirectory()) {
            throw new Exception("Root folder is not a directory.");
        }

        // this.refreshFileIndex();
    }

    /*
    public void refreshFileIndex() {
        for (File fileFolder : Objects.requireNonNull(this.rootFolder.listFiles())) {
            if(!fileFolder.isDirectory()) {
                continue;
            }

            HashSet<Integer> chunks = new HashSet<>();

            for (File chunkFile : Objects.requireNonNull(fileFolder.listFiles())) {
                if (chunkFile.isDirectory() && !chunkFile.getName().matches("\\d+")) {
                    continue;
                }
                chunks.add(Integer.valueOf(chunkFile.getName()));
            }

            savedFiles.put(fileFolder.getName(), chunks);
        }
    }
    */
    public void putChunk(String fileId, Integer chunkId, String content, Integer replicationDegree) throws IOException {
        File fileFolder = new File(rootFolder.getAbsolutePath() + "/" + fileId);
        fileFolder.mkdirs();

        FileWriter fileWriter = new FileWriter(fileFolder.getAbsolutePath() + "/" + chunkId);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(content);
        printWriter.close();

        if (this.savedFiles.containsKey(fileId)) {
            FileInfo chunks = this.savedFiles.get(fileId);
            chunks.addChunk(chunkId, replicationDegree);
        } else {
            FileInfo info = new FileInfo();
            info.addChunk(chunkId, replicationDegree);
            this.savedFiles.put(fileId, info);
        }

    }


    public String getChunk(String fileId, Integer chunkId) throws Exception {

        if (!this.savedFiles.containsKey(fileId) || !this.savedFiles.get(fileId).contains(chunkId)) {
            throw new Exception("File not found");
        }

        return new String(Files.readAllBytes(Paths.get(this.rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkId)));
    }

    public void deleteChunk(String fileId, Integer chunkId) throws Exception {
        if (!this.savedFiles.containsKey(fileId) || !this.savedFiles.get(fileId).contains(chunkId)) {
            throw new Exception("File not found");
        }

        File file = new File(rootFolder.getAbsolutePath() + "/" + fileId + "/" + chunkId);
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

}
