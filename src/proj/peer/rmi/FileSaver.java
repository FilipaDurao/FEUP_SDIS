package proj.peer.rmi;

import proj.peer.Peer;
import proj.peer.message.PutChunkMessage;
import proj.peer.message.handlers.PutChunkHandler;
import proj.peer.utils.SHA256Encoder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class FileSaver {

    private static final int CHUNK_SIZE = 64000;
    private Peer peer;
    private ArrayList<PutChunkHandler> handlers;
    private CountDownLatch chunkSavedSignal;


    FileSaver(Peer peer) {
        this.peer = peer;
        this.handlers = new ArrayList<>();
    }

    private PutChunkHandler sendChunk(Integer replicationDegree, File file, String body, int i) {
        String encodedFileName = SHA256Encoder.encode(file.getName());
        PutChunkMessage msg = new PutChunkMessage(peer.getVersion(), peer.getPeerId(), encodedFileName, i, replicationDegree, body);
        PutChunkHandler handler = new PutChunkHandler(this.peer, msg, this.chunkSavedSignal);
        handler.run();
        this.peer.getControl().subscribe(handler);
        return handler;
    }

    boolean sendFile(String pathname, Integer replicationDegree) {

        File file = new File(pathname);
        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[CHUNK_SIZE];
            double nChunks = data.length() / (double) CHUNK_SIZE;

            this.chunkSavedSignal = new CountDownLatch((int) nChunks + ((nChunks == Math.floor(nChunks)) ? 1 : 0));

            int i = 0;
            for (i = 0; i < nChunks; i++) {
                data.read(buffer);
                this.handlers.add(this.sendChunk(replicationDegree, file, new String(buffer, 0, buffer.length), i));
            }

            if (nChunks == Math.floor(nChunks)) {
                this.handlers.add(this.sendChunk(replicationDegree, file, "", i));
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    boolean waitOperation() {
        if (chunkSavedSignal == null)
            return true;

        try {
            this.chunkSavedSignal.await();
        } catch (InterruptedException e) {
            return false;
        }


        for (PutChunkHandler handler : this.handlers) {
            if (!handler.wasSuccessful()) {
                return false;
            }
        }
        return true;
    }

}