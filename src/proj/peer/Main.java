package proj.peer;

import proj.peer.rmi.RemoteBackup;
import proj.peer.rmi.RemoteBackupInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {

    public static void main(String[] args) {

        try {
            RemoteBackup reBackup = new RemoteBackup();
            RemoteBackupInterface stub = (RemoteBackupInterface) UnicastRemoteObject.exportObject(reBackup,0);
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("RBackup", stub);
            System.out.println("Server Ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
