package proj.peer.message.messages;

public class StoredMessageTCP extends StoredMessage {
    private String hostname;
    private Integer port;

    public StoredMessageTCP(String version, String senderId, String fileId, Integer chunkNo, String hostname, Integer port) {
        super(version, senderId, fileId, chunkNo);
        this.hostname = hostname;
        this.port = port;
    }

    public StoredMessageTCP(String msgStr) throws Exception {
        super();
        String[] msgParts = msgStr.split(Message.CRLF);

        if(msgParts.length < 2) {
            throw new Exception("Malformed message");
        }

        String[] msgHeader = msgParts[0].split("\\s+");
        if (msgHeader.length < 5) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.version = msgHeader[1];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
        this.chunkNo = Integer.valueOf(msgHeader[4]);

        String[] msg2ndHeader = msgParts[1].split("\\s+");
        if (msg2ndHeader.length < 2)
            throw new Exception("Malformed message");

        this.hostname = msg2ndHeader[0];
        this.port = Integer.valueOf(msg2ndHeader[1]);
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }


    @Override
    public String toString() {
        return String.format("%s %s %s %s %d %s%s %d %s",
                this.operation,
                this.getVersion(),
                this.senderId,
                this.fileId,
                this.chunkNo,
                Message.CRLF,
                this.hostname,
                this.port,
                Message.CRLF + Message.CRLF);
    }

}
