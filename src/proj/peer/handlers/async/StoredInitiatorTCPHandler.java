package proj.peer.handlers.async;

import proj.peer.Peer;
import proj.peer.handlers.subscriptions.ChunkSubscription;
import proj.peer.log.NetworkLogger;
import proj.peer.message.messages.*;
import proj.peer.operations.StoredTCPOperation;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class StoredInitiatorTCPHandler extends StoredInitiatorHandler {
    private volatile Integer sendingChunk = 0;
    private final Integer replicationDegree;
    private String filename;
    private final Object lock = new Object();

    public StoredInitiatorTCPHandler(Peer peer, PutChunkMessage msg, CountDownLatch chunkSavedSignal, String filename) {
        super(new ChunkSubscription(StoredMessageTCP.OPERATION, msg.getFileId(), msg.getChunkNo(), msg.getVersion()), peer, msg, chunkSavedSignal);
        replicationDegree = msg.getReplicationDegree();
        this.filename = filename;
    }

    @Override
    public void notify(Message response) {
        if (response instanceof StoredMessageTCP) {
            NetworkLogger.printLog(Level.INFO, "Stored TCP received - " + response.getTruncatedFilename() + " " + ((StoredMessageTCP) response).getChunkNo());
            synchronized (lock) {
                if (this.sendingChunk + this.storedIds.size() < replicationDegree) {
                    this.sendingChunk += 1;
                    // TODO: Start operation to send chunk
                    this.peer.getScheduler().submit(
                            new StoredTCPOperation(
                                    this.peer,
                                    response.getSenderId(),
                                    response.getFileId(),
                                    this.filename,
                                    ((MessageChunk) response).getChunkNo(),
                                    ((StoredMessageTCP) response).getHostname(),
                                    ((StoredMessageTCP) response).getPort(),
                                    this));
                }
            }
        }

    }

    public void markSuccess(String senderId) {
        synchronized (lock) {
            this.storedIds.add(senderId);
            this.sendingChunk -= 1;
            if (this.storedIds.size() >= replicationDegree) {
                this.successful = true;
                this.shutdown();
            }
        }

    }

    public void markFailure() {
        synchronized (lock) {
            this.sendingChunk -= 1;
        }
    }

    public Integer getSuccessfulSend() {
        return this.storedIds.size();
    }
}
