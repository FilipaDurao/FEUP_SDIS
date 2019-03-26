package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.messages.Message;
import proj.peer.message.messages.MessageChunk;
import proj.peer.message.handlers.SubscriptionHandlerInterface;
import proj.peer.message.subscriptions.ChunkSubscription;
import proj.peer.message.subscriptions.FileSubscription;
import proj.peer.message.subscriptions.OperationSubscription;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ControlConnection extends RunnableMC {

    private Peer peer;
    private ConcurrentHashMap<OperationSubscription, SubscriptionHandlerInterface> subscriptions;

    public ControlConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
        this.subscriptions = new ConcurrentHashMap<>();
    }

    public void subscribe(SubscriptionHandlerInterface handler) {
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

                if (msg.getSenderId().equals(peer.getPeerId()) || !msg.getVersion().equals(peer.getVersion())) {
                    continue;
                }

                // Branch point for subscriptions
                if (msg instanceof MessageChunk) {
                    MessageChunk msgC = (MessageChunk) msg;
                    OperationSubscription possibleSub = new ChunkSubscription(msgC.getOperation(), msgC.getFileId(), msgC.getChunkNo());
                    if (subscriptions.containsKey(possibleSub)) {
                        subscriptions.get(possibleSub).notify(msg);
                        continue;
                    }
                }

                OperationSubscription possibleFileSub = new FileSubscription(msg.getOperation(), msg.getFileId());
                OperationSubscription possibleOpSub = new OperationSubscription(msg.getOperation());

                if (subscriptions.containsKey(possibleFileSub)) {
                    subscriptions.get(possibleFileSub).notify(msg);
                    continue;
                } else if (subscriptions.containsKey(possibleOpSub)) {
                    subscriptions.get(possibleOpSub).notify(msg);
                    continue;
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
