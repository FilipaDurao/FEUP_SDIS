package proj.peer.message.messages;

public class PutChunkMessageTCP extends PutChunkMessage {
    private Integer size;

    public PutChunkMessageTCP(String version, String senderId, String fileId, Integer chunkNo, Integer replicationDegree, Integer size) {
        super(version, senderId, fileId, chunkNo, replicationDegree);
        this.size = size;
    }

    public PutChunkMessageTCP(byte[] messageBytes) throws Exception {
        super();
        String message = new String(messageBytes, 0, messageBytes.length);
        String[] msgParts = message.split(Message.CRLF);

        if (msgParts.length < 2) {
            throw new Exception("Malformed message");
        }

        String[] msgHeader = msgParts[0].split("\\s+");
        if (msgHeader.length < 6) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.version = msgHeader[1];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
        this.replicationDegree = Integer.valueOf(msgHeader[5]);

        String[] msg2ndHeader = msgParts[1].split("\\s+");
        if (msg2ndHeader.length < 1)
            throw new Exception("Malformed message");

        this.size = Integer.valueOf(msg2ndHeader[0]);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %d %d %s%d %s",
                this.operation,
                this.getVersion(),
                this.senderId,
                this.fileId,
                this.chunkNo,
                this.replicationDegree,
                Message.CRLF,
                this.size,
                Message.CRLF + Message.CRLF);
    }

    public Integer getSize() {
        return size;
    }
}
