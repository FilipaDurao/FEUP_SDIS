package proj.peer.connection;

import proj.peer.Peer;
import proj.peer.message.Message;
import proj.peer.message.MessageChunk;
import proj.peer.message.StoredMessage;
import proj.peer.message.subscriptions.ChunkSubscription;
import proj.peer.message.subscriptions.FileSubscription;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ControlConnection extends RunnableMC {

    private Peer peer;
    private ConcurrentHashMap<FileSubscription, ConcurrentLinkedDeque<Message> > subscriptions;

    public ControlConnection(Peer peer, String multicast_name, Integer multicast_port_number) throws IOException {
        super(multicast_name, multicast_port_number);
        this.peer = peer;
        this.subscriptions = new ConcurrentHashMap<>();
    }

    public void subscribe(FileSubscription sub) {
        this.subscriptions.put(sub, new ConcurrentLinkedDeque<>());
    }

    public void unsubscribe(FileSubscription sub) {
        this.subscriptions.remove(sub);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message msg = this.getMessage();

                // Branch point for subscriptions of stored messages
                if (msg instanceof MessageChunk) {
                    MessageChunk msgC = (MessageChunk) msg;
                    FileSubscription possibleSub = new ChunkSubscription(msgC.getOperation(), msgC.getFileId(), msgC.getChunkNo());
                    if (subscriptions.containsKey(possibleSub)) {
                        subscriptions.get(possibleSub).add(msg);
                    }
                }


                System.out.println("Message Ignored.");
                System.err.println("TODO: Add treatment for messages of files/chunks saved in this computer");

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
