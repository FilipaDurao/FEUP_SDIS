package proj.peer.message;

public class PutChunkMessage extends Message {

    private final Integer chunkNo;
    private final Integer replicationDegree;
    private final String body;

    public PutChunkMessage(String senderId, String fileId, Integer chunkNo, Integer replicationDegree, String body) {
        super("PUTCHUNK", senderId, fileId);
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
        this.body = body;
    }

    public PutChunkMessage(String msgStr) throws Exception {
        super();
        String[] msgParts = msgStr.split(Message.CRLF);
        if (msgParts.length >= 2)
            this.body = msgParts[2];
        else
            throw new Exception("Malformed PUTCHUNK message: Body is missing");

        String[] msgHeader = msgParts[0].split(" ");
        if (msgHeader.length != 6) {
            throw new Exception("Malformed PUTCHUNK message: Wrong number of arguments");
        }

        this.operation = msgHeader[0];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
        this.replicationDegree = Integer.valueOf(msgHeader[5]);
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.operation).append(" ").append(Message.VERSION).append(" ").append(this.senderId).append(" ").append(this.fileId)
                .append(" ").append(this.chunkNo).append(" ").append(this.replicationDegree).append(" ").append(Message.CRLF)
                .append(Message.CRLF).append(this.body);
        return strBuilder.toString();
    }
}
