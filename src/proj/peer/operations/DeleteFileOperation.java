package proj.peer.operations;

import proj.peer.Peer;
import proj.peer.log.NetworkLogger;

import java.util.logging.Level;

public class DeleteFileOperation implements Runnable {

    private Peer peer;
    private String filename;

    public DeleteFileOperation(Peer peer, String filename) {
        this.peer = peer;
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            this.peer.getFileManager().deleteFile(filename);
            NetworkLogger.printLog(Level.INFO, "File " + filename.substring(0, 5) + " deleted from memory");
        } catch (Exception e) {
            NetworkLogger.printLog(Level.WARNING, "Failed to erase file - " + e.getMessage());
        }
    }
}
