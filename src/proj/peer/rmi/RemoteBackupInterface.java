package proj.peer.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteBackupInterface extends Remote {
    int backup(String pathname, Integer replicationDegree) throws Exception;

    int restore(String filename) throws RemoteException;

    int restore_enh(String filename) throws Exception;

    int backup_enh(String pathname, Integer replicationDegree) throws Exception;

    int delete(String pathname) throws RemoteException;

    int reclaim(Integer diskSpace) throws  RemoteException;

    String state() throws  RemoteException;

}
