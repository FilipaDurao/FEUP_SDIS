package proj.peer.operations;

import proj.peer.log.NetworkLogger;
import proj.peer.manager.FileManager;

import java.util.logging.Level;

public class SaveStructureOperation implements Runnable {

    private FileManager manager;
    private boolean force;

    public SaveStructureOperation(FileManager manager, boolean force) {

        this.manager = manager;
        this.force = force;
    }
    @Override
    public void run() {
        if (force || manager.isStructureChanged()) {
            this.manager.saveFileStructure();
            NetworkLogger.printLog(Level.INFO, "File structure saved");
        }
    }
}
