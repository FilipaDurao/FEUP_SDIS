package proj.peer.message.messages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageWBody extends MessageChunk {
    protected byte[] body;

    public MessageWBody(String version, String operation, String senderId, String fileId, Integer chunkNo, byte[] body) {
        super(version, operation, senderId, fileId, chunkNo);
        this.body = body;
    }

    public MessageWBody() {
        super();
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public byte[] getBytes() {
        byte[] headerBytes = super.getBytes();
        return ByteBuffer.allocate(headerBytes.length + this.body.length).put(headerBytes).put(this.body).array();
    }

    protected byte[][] split(byte[] byteArray, byte[] slicer) {
        int foundElements = 0;

        byte[][] sliced = new byte[2][];
        sliced[0]  = byteArray;
        sliced[1] = new byte[0];

        for (int i = 0; i < byteArray.length; i++) {
            if (foundElements >= slicer.length) {
                // Split byte in array
                sliced[0] = Arrays.copyOfRange(byteArray, 0, i - foundElements);
                sliced[1] = Arrays.copyOfRange(byteArray, i, byteArray.length);
                break;
            }

            if (byteArray[i] == slicer[foundElements]) {
                foundElements++;
                continue;
            }

            foundElements = 0;
        }
        return sliced;
    }
}
