package proj.peer.message;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.message.messages.*;

public class MessageFactory {

    public static Message getMessage(byte[] msgBytes) throws Exception {
        String strMessage = new String(msgBytes, 0, Math.min(MulticastConnection.MAX_HEADER_SIZE, msgBytes.length));
        String[] header = strMessage.split("\\s+");
        String operation = header[0];
        String version = header[1];

        if (version.toUpperCase().equals(Peer.DEFAULT_VERSION)) {

            if (PutChunkMessage.OPERATION.equals(operation.toUpperCase())) {
                return new PutChunkMessage(msgBytes);
            } else if (StoredMessage.OPERATION.equals(operation.toUpperCase())) {
                return new StoredMessage(strMessage);
            } else if (ChunkMessage.OPERATION.equals(operation.toUpperCase())) {
                return new ChunkMessage(msgBytes);
            } else if (GetChunkMessage.OPERATION.equals(operation.toUpperCase())) {
                return new GetChunkMessage(strMessage);
            } else if (DeleteMessage.OPERATION.equals(operation.toUpperCase())) {
                return new DeleteMessage(strMessage);
            } else if (RemovedMessage.OPERATION.equals(operation.toUpperCase())) {
                return new RemovedMessage(strMessage);
            }
        } else {
            if (ChunkMessage.OPERATION.equals(operation.toUpperCase())) {
                return new ChunkMessageTCP(msgBytes);
            } else if (GetChunkMessage.OPERATION.equals(operation.toUpperCase())) {
                return new GetChunkMessage(strMessage);
            } else if (PutChunkMessage.OPERATION.equals(operation.toUpperCase())) {
                return new PutChunkMessage(msgBytes);
            } else if (StoredMessageTCP.OPERATION.equals(operation.toUpperCase())) {
                return new StoredMessageTCP(strMessage);
            }
        }

        throw new Exception("Message type not recognized: " + operation);
    }
}
