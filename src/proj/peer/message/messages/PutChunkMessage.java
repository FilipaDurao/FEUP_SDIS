package proj.peer.message.messages;

import proj.peer.Peer;

import java.nio.ByteBuffer;

public class PutChunkMessage extends MessageWBody {

    public static final String OPERATION = "PUTCHUNK";
    protected Integer replicationDegree;

    public PutChunkMessage(String senderId, String fileId, Integer chunkNo, Integer replicationDegree, byte[] body) {
        super(Peer.DEFAULT_VERSION, OPERATION, senderId, fileId, chunkNo, body);
        this.replicationDegree = replicationDegree;
    }

    public PutChunkMessage(String version, String senderId, String fileId, Integer chunkNo, Integer replicationDegree) {
        super(version, OPERATION, senderId, fileId, chunkNo, new byte[0]);
        this.replicationDegree = replicationDegree;
    }

    public PutChunkMessage(byte[] messageBytes) throws Exception {
        super();
        byte[][] msgParts = this.split(messageBytes, Message.LINE_TERMINATOR_ARRAY);
        if (msgParts.length >= 2) {
            this.body = msgParts[1];
        }
        else {
            this.body = new byte[0];
        }

        String[] msgHeader = new String(msgParts[0], 0, msgParts[0].length).split("\\s+");
        if (msgHeader.length < 6) {
            throw new Exception("Malformed " + this.operation + " message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.version = msgHeader[1];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);
        this.replicationDegree = Integer.valueOf(msgHeader[5]);
    }

    public PutChunkMessage() {

    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %d %d %s%s", this.operation, this.getVersion(), this.senderId, this.fileId, this.chunkNo, this.replicationDegree, Message.CRLF, Message.CRLF);
    }


    public Integer getReplicationDegree() {
        return replicationDegree;
    }

}
