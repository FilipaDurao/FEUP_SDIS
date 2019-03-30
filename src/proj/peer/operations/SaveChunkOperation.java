package proj.peer.operations;

import proj.peer.connection.MulticastConnection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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
                    this.lastChunk = false;
                }
                stream.write(chunk);

            }

        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Saving interrupted");
        }
    }

    public void addChunk(byte[] chunk) {
        this.chunks.add(chunk);
    }
}
