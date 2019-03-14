package proj.peer.message;

public class MessageFactory {

    public static Message getMessage(String msgStr) throws Exception {
        String operation = msgStr.substring(0, msgStr.indexOf(" "));

        if (operation.toUpperCase().equals(PutChunkMessage.OPERATION)) {
            return new PutChunkMessage(msgStr);
        }
        else if (operation.toUpperCase().equals(StoredMessage.OPERATION)) {
            return new StoredMessage(msgStr);
        }

        throw new Exception("Message type not recognized: " + operation);
    }
}
