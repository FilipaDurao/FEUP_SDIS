package proj.peer.message;


public abstract class Message implements MessageInterface {

    public final static String CRLF = "" + (char) 0xD + (char) 0xA;

    protected String operation;
    protected String senderId;
    protected String fileId;
    protected String version;


    Message(String version, String operation, String senderId, String fileId) {
        this.version = version;
        this.operation = operation;
        this.senderId = senderId;
        this.fileId = fileId;

    }

    public Message() {

    }

    public byte[] getBytes() {
        return this.toString().getBytes();
    }

    public String getOperation() {
        return operation;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getFileId() {
        return fileId;
    }

    public String getVersion() {
        return version;
    }

}
