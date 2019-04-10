package proj.peer.operations;

import proj.peer.connection.MulticastConnection;
import proj.peer.log.NetworkLogger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

public class SaveChunkOperation implements Runnable {
    private FileOutputStream stream;
    private BlockingQueue<byte[]> chunks;
    private Boolean lastChunk;

    public SaveChunkOperation(FileOutputStream stream) {
        this.stream = stream;
        this.chunks = new LinkedBlockingDeque<>();
        this.lastChunk = false;
    }

    @Override
    public void run() {
        try {

            while (!this.lastChunk) {
                byte[] chunk = this.chunks.take();
                if (chunk.length < MulticastConnection.CHUNK_SIZE) {
                    this.lastChunk = true;
                }
                stream.write(chunk);
            }
            stream.close();
        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Error saving file - " + e.getMessage());
        } catch (InterruptedException e) {
            NetworkLogger.printLog(Level.SEVERE, "Saving interrupted");
        }
    }

    public void addChunk(byte[] chunk) {
        this.chunks.add(chunk);
    }
}
