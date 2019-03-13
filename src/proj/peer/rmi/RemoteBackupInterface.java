package proj.peer.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteBackupInterface extends Remote {
    int backup(String pathname, Integer replicationDegree) throws Exception;

    int restore(String pathname) throws RemoteException;

    int delete(String pathname) throws RemoteException;

    int reclaim(Integer diskSpace) throws  RemoteException;

    int state() throws  RemoteException;

}
