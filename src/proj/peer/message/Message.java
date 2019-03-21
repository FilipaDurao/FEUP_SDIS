package proj.peer.message;


public abstract class Message implements MessageInterface {

    public final static String CRLF = "" + (char) 0xD + (char) 0xA;
    public final static Integer VERSION = 1;

    protected String operation;
    protected String senderId;
    protected String fileId;


    Message(String operation, String senderId, String fileId) {
        this.operation = operation;
        this.senderId = senderId;
        this.fileId = fileId;

        System.err.println("TODO: Add version, currently hard coded");
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
}
