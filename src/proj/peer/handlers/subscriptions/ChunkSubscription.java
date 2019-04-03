package proj.peer.handlers.subscriptions;

public class ChunkSubscription extends FileSubscription{
    private Integer chunkNo;

    public ChunkSubscription(String operation, String fileId, Integer chunkNo, String version) {
        super(operation, fileId, version);
        this.chunkNo = chunkNo;
    }

    public Integer getChunkNo() {
        return chunkNo;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ChunkSubscription) {
            ChunkSubscription other = (ChunkSubscription) o;
            return super.equals(o) && this.chunkNo.equals(other.getChunkNo());
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return (this.operation + this.fileId + chunkNo + version).hashCode();
    }
}
