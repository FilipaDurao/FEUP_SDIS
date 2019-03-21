package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.Message;
import proj.peer.message.MessageChunk;
import proj.peer.message.subscriptions.ChunkSubscription;
import proj.peer.message.subscriptions.FileSubscription;
import proj.peer.message.subscriptions.OperationSubscription;
import proj.peer.message.subscriptions.handlers.SubscriptionHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ControlConnection extends RunnableMC {

    private Peer peer;
    private ConcurrentHashMap<OperationSubscription, SubscriptionHandler> subscriptions;

    public ControlConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
        this.subscriptions = new ConcurrentHashMap<>();
    }

    public void subscribe(SubscriptionHandler handler) {
        this.subscriptions.put(handler.getSub(), handler);
    }

    public void unsubscribe(OperationSubscription sub) {
        if (subscriptions.containsKey(sub))
            this.subscriptions.remove(sub);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message msg = this.getMessage();

                if (msg.getSenderId().equals(peer.getPeerId())) {
                    continue;
                }

                // Branch point for subscriptions
                if (msg  instanceof  MessageChunk) {
                    MessageChunk msgC = (MessageChunk) msg;
                    FileSubscription possibleSub = new ChunkSubscription(msgC.getOperation(), msgC.getFileId(), msgC.getChunkNo());
                    if (subscriptions.containsKey(possibleSub)) {
                        subscriptions.get(possibleSub).notify(msg);
                        continue;
                    }
                }


                System.out.println(String.format("Message Ignored: %s %s %s", msg.getOperation(), msg.getSenderId(), msg.getFileId()));
                System.err.println("TODO: Add treatment for messages of files/chunks saved in this computer");
                System.err.println("HINT: Add a handler for each message");

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
