package client;

import interfaces.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cuneiform.Citizen;
import cuneiform.FoundDate;
import cuneiform.FoundDateList;

public class MockCoalMine {
	String host;
	public Server server;
	int citizenCap = 4;
	int numCitizens = 0;
	ExecutorService threadPool;
	public MockCoalMine(String hostname) {
		host = hostname;
	}
	
	private boolean load(String hostname) {
			
		return true;
	}
	

	
	public boolean roomLeft() throws RemoteException {		
		if (numCitizens >= citizenCap){
			return false;
		}
		else{
			return true;
		}
	}
	
	public boolean sendToMine(Citizen cit, List<FoundDate> attestations) {
		numCitizens++;
		return true;
	}
	

	//XXX
    public static final String dbHost = "jdbc:mysql://cuneiform.cs.wwu.edu/cuneiform";
    public static final String dbUser = "dingo";
    public static final String dbPass = "hungry!";
    
	//XXX
    private static void registerMySqlDriver()
    {
    	
    }
	
	
    public String toString(){
    	return "[Num of Citizens in me: " + numCitizens + "]";
    }

	
	
}
