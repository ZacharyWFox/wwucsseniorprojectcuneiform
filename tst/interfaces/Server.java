package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;
import cuneiform.KnownDate;
/**
 * Remote interface for RMI purposes
 * @author ZacharyWFox
 */
public interface Server extends Remote {
	float live(Citizen cit, List<FoundDate> attestations) throws RemoteException;
	void setAllKnownDates(List<KnownDate> dates) throws RemoteException;
	String getName() throws RemoteException;
	void setName(String name) throws RemoteException;
	
	String getHostName() throws RemoteException;
	String getIPAddressString() throws RemoteException;
	int getMaxCitizens() throws RemoteException;
	int getNumCitizens() throws RemoteException;
}
