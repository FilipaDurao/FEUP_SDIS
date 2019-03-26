package proj.peer.message.messages;

public class PutChunkMessage extends MessageChunk {

    public static final String OPERATION = "PUTCHUNK";
    private Integer replicationDegree;
    private String body;

    public PutChunkMessage(String version, String senderId, String fileId, Integer chunkNo, Integer replicationDegree, String body) {
        super(version, OPERATION, senderId, fileId, chunkNo);
        this.replicationDegree = replicationDegree;
        this.body = body;
    }

    public PutChunkMessage(String msgStr) throws Exception {
        super();
        String[] msgParts = msgStr.split(Message.CRLF + Message.CRLF);
        if (msgParts.length >= 2)
            this.body = msgParts[1];
        else
            this.body = "";

        String[] msgHeader = msgParts[0].split(" ");
        if (msgHeader.length != 6) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.version = msgHeader[1];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
        this.replicationDegree = Integer.valueOf(msgHeader[5]);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %d %d %s%s%s", this.operation, this.getVersion(), this.senderId, this.fileId, this.chunkNo, this.replicationDegree, Message.CRLF, Message.CRLF, this.body);
    }


    public Integer getReplicationDegree() {
        return replicationDegree;
    }

    public String getBody() {
        return body;
    }
}
