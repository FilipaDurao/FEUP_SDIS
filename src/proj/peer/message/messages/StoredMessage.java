package proj.peer.message.messages;

public class StoredMessage extends  MessageChunk{

    public static final String OPERATION = "STORED";

    public StoredMessage(String version, String senderId, String fileId, Integer chunkNo) {
        super(version, OPERATION, senderId, fileId, chunkNo);
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
        this.version = msgHeader[1];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %d %s%s", this.operation, this.getVersion(), this.senderId, this.fileId, this.chunkNo, Message.CRLF, Message.CRLF);
    }

}
