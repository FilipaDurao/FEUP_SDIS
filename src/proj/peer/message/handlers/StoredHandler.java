package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.manager.FileManager;
import proj.peer.message.Message;
import proj.peer.message.StoredMessage;
import proj.peer.message.subscriptions.OperationSubscription;

public class StoredHandler implements SubscriptionHandler {

    private OperationSubscription operationSubscription;
    private FileManager fileManager;

    public StoredHandler(Peer peer) {
        this.operationSubscription = new OperationSubscription(StoredMessage.OPERATION);
        fileManager = peer.getFileManager();
    }

    @Override
    public void notify(Message msg) {
        if (msg instanceof StoredMessage) {
            System.out.println(String.format("Message Received: %s %s %s", msg.getOperation(), msg.getSenderId(), msg.getFileId()));
            StoredMessage storedMessage = (StoredMessage) msg;
            fileManager.storeChunkPeer(storedMessage.getFileId(), storedMessage.getChunkNo(), storedMessage.getSenderId());
        }
    }

    @Override
    public OperationSubscription getSub() {
        return this.operationSubscription;
    }
}
