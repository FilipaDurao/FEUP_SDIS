package proj.peer.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class FileInfo implements Serializable {

    private HashMap<Integer, ChunkInfo> chunks;

    public FileInfo() {
        this.chunks = new HashMap<>();
    }

    public void addChunk(Integer chunkNumber, Integer replicationDegree) {
        this.chunks.put(chunkNumber, new ChunkInfo(chunkNumber, replicationDegree));
    }

    public Collection<ChunkInfo> getChunks() {
        return this.chunks.values();
    }

    public boolean contains(Integer chunkNumber) {
        return this.chunks.containsKey(chunkNumber);
    }

    public void addPeerId(Integer chunkNumber, String peerId) {
        if (this.chunks.containsKey(chunkNumber))
            this.chunks.get(chunkNumber).addPeer(peerId);
    }
}
