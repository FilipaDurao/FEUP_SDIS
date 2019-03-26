package proj.peer.utils;

public class ChunkIdentifier {

    private String fileId;
    private Integer chunkNo;

    ChunkIdentifier(String fileId, Integer chunkNo) {

        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public int hashCode() {
        return (chunkNo + fileId).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChunkIdentifier) {
            ChunkIdentifier other = (ChunkIdentifier) o;
            return other.chunkNo.equals(this.chunkNo) && this.fileId.equals(other.fileId);
        }
        return false;
    }
}
