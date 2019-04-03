package proj.peer.message.messages;

public class DeleteMessage extends Message {
    public static final String OPERATION = "DELETE";

    public DeleteMessage(String version, String senderId, String fileId) {
        super(version, OPERATION, senderId, fileId);
    }

    public DeleteMessage(String msgStr) throws Exception {
        super();

        String[] msgHeader = msgStr.split(" ");
        if (msgHeader.length < 5) {
            throw new Exception("Malformed OPERATION message: Wrong number of arguments");
        }

        this.operation = OPERATION;
        this.version = msgHeader[1];
        this.senderId = msgHeader[2];
        this.fileId = msgHeader[3];
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s%s", this.operation, this.getVersion(), this.senderId, this.fileId, Message.CRLF, Message.CRLF);
    }
}
