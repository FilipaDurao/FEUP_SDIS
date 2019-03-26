package proj.peer.manager;

import java.util.HashSet;

public class ChunkInfo {
    private Integer chunkNumber;
    private Integer replicationDegree;
    private HashSet<String> peerIds;

    public ChunkInfo(Integer chunkNumber, Integer replicationDegree) {

        this.chunkNumber = chunkNumber;
        this.replicationDegree = replicationDegree;
        this.peerIds = new HashSet<>();
    }

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public Integer getReplicationDegree() {
        return replicationDegree;
    }

    public void addPeer(String id) {
        if (!this.peerIds.contains(id))
            this.peerIds.add(id);
    }

    public int getNumberOfSaves() {
        return this.peerIds.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChunkInfo) {
            return ((ChunkInfo) o).getChunkNumber() == this.getChunkNumber();
        }
        return false;
    }
}
