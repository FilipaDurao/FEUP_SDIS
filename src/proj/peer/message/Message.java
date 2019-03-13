package proj.peer.message;


public class Message implements MessageInterface {

    public final static String CRLF = "" + (char) 0xD + (char) 0xA;

    private String operation;
    private String senderId;
    private String fileId;
    private Integer chunkNo;
    private Integer replicationDegree;
    private String body;


    public Message(String operation, String senderId, String fileId, Integer chunkNo, String body) {
        this.operation = operation;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = null;
        this.body = body;
    }


    public Message(String operation, String senderId, String fileId, Integer chunkNo) {
        this.operation = operation;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = null;
        this.body = "";
    }

    public Message(String operation, String senderId, String fileId, Integer chunkNo, Integer replicationDegree, String body) {
        this.operation = operation;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
        this.body = body;
    }

    public Message(String operation, String senderId, String fileId, Integer chunkNo, Integer replicationDegree) {
        this.operation = operation;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
        this.body = "";
    }

    public Message(String msgStr) throws Exception {
        String[] msgParts = msgStr.split(Message.CRLF);
        if(msgParts.length >= 2)
            this.body = msgParts[2];
        else
            this.body = "";

        String[] msgHeader = msgParts[0].split(" ");
        if(msgHeader.length != 5 && msgHeader.length != 6) {
            throw  new Exception("Wrong number of arguments");
        }

        this.operation = msgHeader[0];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
        if(msgHeader.length == 6) {
            this.replicationDegree = Integer.valueOf(msgHeader[5]);
        } else {
            this.replicationDegree = null;
        }

    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getChunkNo() {
        return chunkNo;
    }

    public void setChunkNo(Integer chunkNo) {
        this.chunkNo = chunkNo;
    }

    public Integer getReplicationDegree() {
        return replicationDegree;
    }

    public void setReplicationDegree(Integer replicationDegree) {
        this.replicationDegree = replicationDegree;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getBytes() {
        return this.toString().getBytes();
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.operation).append(" ").append(1).append(" ").append(this.senderId).append(" ").append(this.fileId)
                .append(" ").append(this.chunkNo);

        if (this.replicationDegree != null) {
            strBuilder.append(" ").append(this.replicationDegree);
        }

        strBuilder.append(" ").append(Message.CRLF)
                .append(Message.CRLF).append(this.body);
        return strBuilder.toString();
    }
}
