package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;

public interface Server extends Remote {
	float live(Citizen cit, List<FoundDate> attestations) throws RemoteException;
	String getName();
	void setName(String name);
	
	String getHostName();
	String getIPAddressString();
}
