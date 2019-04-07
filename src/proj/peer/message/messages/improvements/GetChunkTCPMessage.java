package proj.peer.message.messages.improvements;

import proj.peer.message.messages.GetChunkMessage;
import proj.peer.message.messages.Message;

public class GetChunkTCPMessage extends GetChunkMessage {
    private String hostname;
    private Integer port;

    public GetChunkTCPMessage(String version, String senderId, String fileId, Integer chunkNo, String hostname, Integer port) {
        super(version, senderId, fileId, chunkNo);
        this.hostname = hostname;
        this.port = port;
    }

    public GetChunkTCPMessage(String msgStr) throws Exception {
        super();

        String[] msgParts = msgStr.split(Message.CRLF + Message.CRLF);
        if (msgParts.length > 3)
            throw new Exception("Malformed OPERATION message: Body included");

        String[] msgHeader = msgParts[0].split("\\s+");
        if (msgHeader.length < 5) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.version = msgHeader[1];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);

        msgHeader = msgParts[1].split("\\s+");
        if (msgHeader.length < 2) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.hostname = msgHeader[0];
        this.port = Integer.valueOf(msgHeader[1]);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %d %s%s %d %s",
                this.operation,
                this.getVersion(),
                this.senderId,
                this.fileId,
                this.chunkNo,
                Message.CRLF + Message.CRLF,
                this.hostname,
                this.port,
                Message.CRLF + Message.CRLF);
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }
}
