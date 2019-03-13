package proj.peer.message;

public class BackupMessage extends Message {

    private String senderId;
    private String fileId;
    private Integer chunkNo;
    private Integer replicationDegree;
    private String body;

    public BackupMessage(String senderId, String fileId, Integer chunkNo, Integer replicationDegree, String body) {

        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
        this.body = body;
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

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("PUTCHUNK ").append(1).append(" ").append(this.senderId).append(" ").append(this.fileId)
                .append(" ").append(this.chunkNo).append(" ").append(this.replicationDegree)
                .append(" ").append(Message.CRLF)
                .append(Message.CRLF).append(this.body);
        return strBuilder.toString();
    }
}
