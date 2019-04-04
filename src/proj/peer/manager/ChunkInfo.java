package proj.peer.manager;

import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkInfo implements Serializable {
    private Integer chunkNumber;
    private Integer replicationDegree;
    private Integer size;
    private ConcurrentHashMap<String, Boolean> peerIds;

    public ChunkInfo(Integer chunkNumber, Integer replicationDegree, Integer size) {

        this.chunkNumber = chunkNumber;
        this.replicationDegree = replicationDegree;
        this.size = size;
        this.peerIds = new ConcurrentHashMap<>();
    }

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public Integer getReplicationDegree() {
        return replicationDegree;
    }

    public void addPeer(String id) {
        this.peerIds.put(id, true);
    }

    public int getNumberOfSaves() {
        return this.peerIds.size();
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChunkInfo) {
            return ((ChunkInfo) o).getChunkNumber().equals(this.getChunkNumber());
        }
        return false;
    }

    public void removePeer(String peerId) {
        this.peerIds.remove(peerId);
    }
}
