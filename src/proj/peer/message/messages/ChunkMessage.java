package proj.peer.message.messages;

public class ChunkMessage extends MessageChunk{

    public static final String OPERATION = "CHUNK";
    private String body;

    public ChunkMessage(String version, String senderId, String fileId, Integer chunkNo, String body) {
        super(version, OPERATION, senderId, fileId, chunkNo);
        this.body = body;
    }

    public ChunkMessage(String msgStr) throws Exception {
        super();
        String[] msgParts = msgStr.split(Message.CRLF + Message.CRLF);
        if (msgParts.length >= 2)
            this.body = msgParts[1];
        else
            this.body = "";

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
        return String.format("%s %s %s %s %d %s%s%s", this.operation, this.getVersion(), this.senderId, this.fileId, this.chunkNo, Message.CRLF, Message.CRLF, this.body);
    }



    public String getBody() {
        return body;
    }
}
