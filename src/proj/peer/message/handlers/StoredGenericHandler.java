package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.manager.FileManager;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.StoredMessage;
import proj.peer.message.subscriptions.OperationSubscription;

public class StoredGenericHandler implements SubscriptionHandlerInterface {

    private OperationSubscription operationSubscription;
    private FileManager fileManager;

    public StoredGenericHandler(Peer peer) {
        this.operationSubscription = new OperationSubscription(StoredMessage.OPERATION);
        fileManager = peer.getFileManager();
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof StoredMessage) {
            System.out.println(String.format("Message Received: %s %s %s", msg.getOperation(), msg.getSenderId(), msg.getFileId()));
            StoredMessage storedMessage = (StoredMessage) msg;
            fileManager.storeChunkPeer(msg.getFileId(), storedMessage.getChunkNo(), storedMessage.getSenderId());
        }
    }

    @Override
    public OperationSubscription getSub() {
        return this.operationSubscription;
    }
}
