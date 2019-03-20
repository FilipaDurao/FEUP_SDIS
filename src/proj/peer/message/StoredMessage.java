package proj.peer.message;

public class StoredMessage extends  Message{

    public static final String OPERATION = "STORED";
    private final Integer chunkNo;

    public StoredMessage(String senderId, String fileId, Integer chunkNo) {
        super(OPERATION, senderId, fileId);
        this.chunkNo = chunkNo;
    }

    public StoredMessage(String msgStr) throws Exception {
        super();
        String[] msgParts = msgStr.split(Message.CRLF);
        if (msgParts.length >= 3)
            throw new Exception("Malformed OPERATION message: Body included");

        String[] msgHeader = msgParts[0].split(" ");
        if (msgHeader.length != 5) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
    }

    @Override
    public String toString() {
        return String.format("%s %d %s %s %d %s%s", this.operation, Message.VERSION, this.senderId, this.fileId, this.chunkNo, Message.CRLF, Message.CRLF);
    }

    public Integer getChunkNo() {
        return chunkNo;
    }
}
