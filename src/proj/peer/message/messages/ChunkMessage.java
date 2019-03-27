package proj.peer.message.messages;

public class ChunkMessage extends MessageWBody{

    public static final String OPERATION = "CHUNK";

    public ChunkMessage(String version, String senderId, String fileId, Integer chunkNo, byte[] body) {
        super(version, OPERATION, senderId, fileId, chunkNo, body);
    }

    public ChunkMessage(byte[] messageBytes) throws Exception {
        super();
        byte[][] msgParts = this.split(messageBytes, Message.LINE_TERMINATOR_ARRAY);
        if (msgParts.length >= 2)
            this.body = msgParts[1];
        else
            this.body = new byte[0];

        String[] msgHeader = new String(msgParts[0], 0, msgParts[0].length).split(" ");
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
