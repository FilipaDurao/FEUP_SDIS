package proj.peer.message.subscriptions;

public class ChunkSubscription extends FileSubscription{
    private Integer chunkNo;

    public ChunkSubscription(String operation, String fileId, Integer chunkNo) {
        super(operation, fileId);
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
        return false;
    }
}
