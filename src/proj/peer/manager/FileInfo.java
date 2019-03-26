package proj.peer.manager;

import java.util.HashMap;
import java.util.HashSet;

public class FileInfo {

    private HashMap<Integer, ChunkInfo> chunks;

    public FileInfo() {
        this.chunks = new HashMap<>();
    }

    public void addChunk(Integer chunkNumber, Integer replicationDegree) {
        this.chunks.put(chunkNumber, new ChunkInfo(chunkNumber, replicationDegree));
    }

    public HashSet<ChunkInfo> getChunks() {
        return (HashSet<ChunkInfo>) this.chunks.values();
    }

    public boolean contains(Integer chunkNumber) {
        return this.chunks.containsKey(chunkNumber);
    }

    public void addPeerId(Integer chunkNumber, String peerId) {
        if (this.chunks.containsKey(chunkNumber))
            this.chunks.get(chunkNumber).addPeer(peerId);
    }
}
