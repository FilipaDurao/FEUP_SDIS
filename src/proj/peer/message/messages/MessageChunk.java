package proj.peer.message.messages;


public abstract class MessageChunk extends Message{
    protected Integer chunkNo;

    public MessageChunk(String version, String operation, String senderId, String fileId, Integer chunkNo) {
        super(version, operation, senderId, fileId);
        this.chunkNo = chunkNo;
    }

    public MessageChunk() {
        super();
    }

    public Integer getChunkNo() {
        return chunkNo;
    }

}
