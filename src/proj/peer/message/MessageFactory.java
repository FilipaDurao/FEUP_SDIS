package proj.peer.message;

public class MessageFactory {

    public static Message getMessage(String msgStr) throws Exception {
        String operation = msgStr.substring(0, msgStr.indexOf(" "));

        if (operation.toUpperCase().equals("PUTCHUNK")) {
            return new PutChunkMessage(msgStr);
        }

        throw new Exception("Message type not recognized: " + operation);
    }
}
