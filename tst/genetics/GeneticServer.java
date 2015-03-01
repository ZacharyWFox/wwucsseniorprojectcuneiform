package genetics;

import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;

public class GeneticServer implements Server {
	String name = "Default";
	String hostName;
	Executor threads;
	
	public GeneticServer() {
		super();
		//TODO: initialize threads
	}
	
	
	
	@Override
	public float live(Citizen cit, List<FoundDate> attestations)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		try {
			
			String key = "Server";
			Server jenkins = new GeneticServer();
			
			
			
			if (args.length > 2)
				jenkins.setName(args[2]);
			
			
			Server stub = (Server) UnicastRemoteObject.exportObject(jenkins);
			
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(key, stub);
		} catch (Exception e) {
			System.out.println("Failed to start GeneticServer");
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public String getHostName() {
		// TODO Auto-generated method stub
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getIPAddressString() {
		// TODO Auto-generated method stub
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
