package proj.client;

import proj.peer.rmi.RemoteBackup;
import proj.peer.rmi.RemoteBackupInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Usage:  <host_name> <operation> <oper_1> <oper_2>");
            return;
        }

        String host = args[0];
        String operation = args[1];

        try {
            Registry reg = LocateRegistry.getRegistry(host);
            RemoteBackupInterface remoteBackup = (RemoteBackupInterface) reg.lookup("RBackup");
            Integer res = remoteBackup.backup("test.pdf", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
