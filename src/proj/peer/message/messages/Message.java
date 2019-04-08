package proj.peer.message.messages;


import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public abstract class Message implements MessageInterface, Serializable {

    public final static String CRLF = "" + (char) 0xD + (char) 0xA;
    public final static byte[] LINE_TERMINATOR_ARRAY = {0xD, 0xA, 0xD, 0xA};

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

    public String getTruncatedFilename() {
        return this.fileId.substring(0, Math.min(this.fileId.length(), 5));
    }

}
