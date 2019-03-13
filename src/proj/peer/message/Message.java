package proj.peer.message;

public abstract class Message implements MessageInterface {

    protected final static String CRLF = "" + (char) 0xD + (char) 0xA;

    public byte[] getBytes() {
        return this.toString().getBytes();
    }
}
