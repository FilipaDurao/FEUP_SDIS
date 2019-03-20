package proj.peer.manager;

public class ChunkInfo {
    private Integer chunkNumber;
    private Integer replicationDegree;

    public ChunkInfo(Integer chunkNumber, Integer replicationDegree) {

        this.chunkNumber = chunkNumber;
        this.replicationDegree = replicationDegree;
    }

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public Integer getReplicationDegree() {
        return replicationDegree;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChunkInfo) {
            return ((ChunkInfo) o).getChunkNumber() == this.getChunkNumber();
        }
        return false;
    }
}
