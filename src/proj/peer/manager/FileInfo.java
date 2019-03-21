package proj.peer.manager;

import java.util.HashSet;

public class FileInfo {

    private HashSet<ChunkInfo> chunks;

    public FileInfo() {
        this.chunks = new HashSet<>();
    }

    public void addChunk(Integer chunkNumber, Integer replicationDegree) {
        this.chunks.add(new ChunkInfo(chunkNumber, replicationDegree));
    }

    public HashSet<ChunkInfo> getChunks() {
        return this.chunks;
    }

    public boolean contains(Integer chunkNumber) {
        return this.chunks.contains(new ChunkInfo(chunkNumber, 0));
    }
}
