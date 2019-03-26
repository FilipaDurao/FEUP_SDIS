package proj.peer.message.handlers;

import proj.peer.Peer;
import proj.peer.connection.BackupConnection;
import proj.peer.connection.ControlConnection;
import proj.peer.message.Message;
import proj.peer.message.PutChunkMessage;
import proj.peer.message.StoredMessage;
import proj.peer.message.subscriptions.ChunkSubscription;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PutChunkHandler implements Runnable, SubscriptionHandler {

    private final ChunkSubscription sub;
    private ScheduledThreadPoolExecutor scheduler;
    private BackupConnection backupConnection;
    private ControlConnection controlConnection;
    private PutChunkMessage msg;
    private CountDownLatch chunkSavedSignal;
    private HashSet<String> storedIds;
    private Future future;
    private Integer attempts;
    private Boolean successful;

    public PutChunkHandler(Peer peer, PutChunkMessage msg, CountDownLatch chunkSavedSignal) {
        this.backupConnection = peer.getBackup();
        this.controlConnection = peer.getControl();
        this.scheduler = peer.getScheduler();
        this.msg = msg;
        this.chunkSavedSignal = chunkSavedSignal;
        this.storedIds = new HashSet<>();
        this.attempts = 0;
        this.successful = false;

        this.sub = new ChunkSubscription(StoredMessage.OPERATION, msg.getFileId(), msg.getChunkNo());
    }

    @Override
    public void run() {
        try {
            this.backupConnection.sendMessage(msg);
            if (this.attempts <= 5 && !this.successful) {
                this.future = this.scheduler.schedule(this, (long) Math.pow(2, this.attempts), TimeUnit.SECONDS);
                this.attempts++;
            }
            else {
                System.err.println("Failed PUTCHUNK protocol");
                this.controlConnection.unsubscribe(this.sub);
                if (this.chunkSavedSignal != null)
                    this.chunkSavedSignal.countDown();
            }
        } catch (IOException e) {
            System.err.println("Error sending scheduled message");
        }

    }

    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }


    @Override
    public void notify(Message response) {
        if (response instanceof StoredMessage) {
            if (!storedIds.contains(response.getSenderId())) {
                this.storedIds.add(response.getSenderId());
                if (this.storedIds.size() >= this.msg.getReplicationDegree()) {
                    this.cancel();
                    this.controlConnection.unsubscribe(this.sub);
                    this.successful = true;
                    if (this.chunkSavedSignal != null)
                        this.chunkSavedSignal.countDown();
                }
            }
        }
    }

    @Override
    public ChunkSubscription getSub() {
        return sub;
    }

    @Override
    public boolean wasSuccessful() {
        return this.successful;
    }
}
