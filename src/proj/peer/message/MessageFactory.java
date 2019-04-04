package proj.peer.message;

import proj.peer.message.messages.*;

public class MessageFactory {

    public static Message getMessage(byte[] msgStr) throws Exception {
        String strMessage = new String(msgStr, 0, msgStr.length);
        String operation = strMessage.substring(0, strMessage.indexOf(" "));

        if (operation.toUpperCase().equals(PutChunkMessage.OPERATION)) {
            return new PutChunkMessage(msgStr);
        }
        else if (operation.toUpperCase().equals(StoredMessage.OPERATION)) {
            return new StoredMessage(strMessage);
        }
        else if (operation.toUpperCase().equals(ChunkMessage.OPERATION)) {
            return new ChunkMessage(msgStr);
        }
        else if (operation.toUpperCase().equals(GetChunkMessage.OPERATION)) {
            return new GetChunkMessage(strMessage);
        }
        else if (operation.toUpperCase().equals(DeleteMessage.OPERATION)) {
            return new DeleteMessage(strMessage);
        }
        else if (operation.toUpperCase().equals(RemovedMessage.OPERATION)) {
            return new RemovedMessage(strMessage);
        }
        throw new Exception("Message type not recognized: " + operation);
    }
}
