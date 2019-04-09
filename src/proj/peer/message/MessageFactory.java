package proj.peer.message;

import proj.peer.Peer;
import proj.peer.connection.MulticastConnection;
import proj.peer.message.messages.*;

public class MessageFactory {

    public static Message getMessage(byte[] msgStr) throws Exception {
        String strMessage = new String(msgStr, 0, Math.min(MulticastConnection.MAX_HEADER_SIZE, msgStr.length));
        String[] header = strMessage.split("\\s+");
        String operation = header[0];
        String version = header[1];

        if (version.toUpperCase().equals(Peer.DEFAULT_VERSION)) {

            if (operation.toUpperCase().equals(PutChunkMessage.OPERATION)) {
                return new PutChunkMessage(msgStr);
            } else if (operation.toUpperCase().equals(StoredMessage.OPERATION)) {
                return new StoredMessage(strMessage);
            } else if (operation.toUpperCase().equals(ChunkMessage.OPERATION)) {
                return new ChunkMessage(msgStr);
            } else if (operation.toUpperCase().equals(GetChunkMessage.OPERATION)) {
                return new GetChunkMessage(strMessage);
            } else if (operation.toUpperCase().equals(DeleteMessage.OPERATION)) {
                return new DeleteMessage(strMessage);
            } else if (operation.toUpperCase().equals(RemovedMessage.OPERATION)) {
                return new RemovedMessage(strMessage);
            }
        } else {
            if (operation.toUpperCase().equals(ChunkMessage.OPERATION)) {
                return new ChunkMessageTCP(msgStr);
            } else if (operation.toUpperCase().equals(GetChunkMessage.OPERATION)) {
                return new GetChunkMessage(strMessage);
            }
        }

        throw new Exception("Message type not recognized: " + operation);
    }
}
