package proj.peer.message;

public abstract class MessageChunk extends Message{
    protected Integer chunkNo;

    public MessageChunk(String operation, String senderId, String fileId, Integer chunkNo) {
        super(operation, senderId, fileId);
        this.chunkNo = chunkNo;
    }

    public MessageChunk() {
        super();
    }

    public Integer getChunkNo() {
        return chunkNo;
    }
}
