package proj.peer.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileInfo implements Serializable {

    private ConcurrentHashMap<Integer, ChunkInfo> chunks;
    private String filename;

    public FileInfo(String filename) {
        this.filename = filename;
        this.chunks = new ConcurrentHashMap<>();
    }

    public void addChunk(Integer chunkNumber, Integer replicationDegree, Integer size) {
        this.chunks.put(chunkNumber, new ChunkInfo(chunkNumber, replicationDegree, size));
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

    public int deleteChunk(Integer chunkId) {
        if (this.chunks.containsKey(chunkId) ) {
            int size = this.chunks.get(chunkId).getSize();
            this.chunks.remove(chunkId);
            return size;
        }
        return 0;
    }

    public int getSize(Integer chunkId) {
        return this.chunks.get(chunkId).getSize();
    }

    public int getSize() {
        int size = 0;
        for (Map.Entry<Integer, ChunkInfo> entry : chunks.entrySet()) {
            size += entry.getValue().getSize();
        }
        return size;
    }

    public ChunkInfo getChunkInfo(Integer chunkNo) throws Exception {
        if (contains(chunkNo)) {
            return this.chunks.get(chunkNo);
        }
        throw  new Exception("Chunk not found");
    }

    public void removePeerId(Integer chunkNumber, String peerId) {
        if (this.chunks.containsKey(chunkNumber))
            this.chunks.get(chunkNumber).removePeer(peerId);
    }

    public String getFilename() {
        return filename;
    }
}
