package proj.peer;

import proj.peer.rmi.RemoteBackup;
import proj.peer.rmi.RemoteBackupInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Peer <peer_ap>");
            System.exit(-1);
        }

        String peerId = args[0];
        try {
            RemoteBackup reBackup = new RemoteBackup(peerId);
            RemoteBackupInterface stub = (RemoteBackupInterface) UnicastRemoteObject.exportObject(reBackup,0);
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("RBackup" + peerId, stub);
            System.out.println("Server Ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
