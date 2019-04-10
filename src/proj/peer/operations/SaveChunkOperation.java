package proj.peer.operations;

import proj.peer.connection.MulticastConnection;
import proj.peer.log.NetworkLogger;
import proj.peer.utils.ChunkIdentifier;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

public class SaveChunkOperation implements Runnable {
    private RandomAccessFile stream;
    private int nChunks;
    private BlockingQueue<ChunkIdentifier> chunks;
    private HashSet<Integer> unorderedChunks;
    private Boolean lastChunk;

    public SaveChunkOperation(RandomAccessFile stream, int nChunks) {
        this.stream = stream;
        this.nChunks = nChunks;
        this.chunks = new LinkedBlockingDeque<>();
        this.lastChunk = false;
        this.unorderedChunks = new HashSet<>();
    }

    @Override
    public void run() {
        try {
            while(!ended()) {
                ChunkIdentifier chunk = this.chunks.take();
                stream.seek(chunk.getChunkNo() * MulticastConnection.CHUNK_SIZE);
                stream.write(chunk.getBody());
                this.unorderedChunks.add(chunk.getChunkNo());
            }

        } catch (IOException e) {
            NetworkLogger.printLog(Level.SEVERE, "Error saving file - " + e.getMessage());
        } catch (InterruptedException e) {
            NetworkLogger.printLog(Level.SEVERE, "Saving interrupted");
        }
    }

    private boolean ended() {
        for (int i = 0; i < nChunks; i++) {
            if(!this.unorderedChunks.contains(i)) {
                return false;
            }
        }
        return true;
    }

    public void addChunk(Integer chunkNo, byte[] chunk) {
        this.chunks.add(new ChunkIdentifier(chunkNo, chunk));
    }
}
