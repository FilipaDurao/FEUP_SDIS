package proj.peer.message;

public class PutChunkMessage extends Message {

    public static final String OPERATION = "PUTCHUNK";
    private final Integer chunkNo;
    private final Integer replicationDegree;
    private final String body;

    public PutChunkMessage(String senderId, String fileId, Integer chunkNo, Integer replicationDegree, String body) {
        super(OPERATION, senderId, fileId);
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
        this.body = body;
    }

    public PutChunkMessage(String msgStr) throws Exception {
        super();
        String[] msgParts = msgStr.split(Message.CRLF);
        if (msgParts.length >= 3)
            this.body = msgParts[2];
        else
            throw new Exception("Malformed OPERATION message: Body is missing");

        String[] msgHeader = msgParts[0].split(" ");
        if (msgHeader.length != 6) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
        this.replicationDegree = Integer.valueOf(msgHeader[5]);
    }

    @Override
    public String toString() {
        return String.format("%s %d %s %s %d %d %s%s%s", this.operation, Message.VERSION, this.senderId, this.fileId, this.chunkNo, this.replicationDegree, Message.CRLF, Message.CRLF, this.body);
    }
}
