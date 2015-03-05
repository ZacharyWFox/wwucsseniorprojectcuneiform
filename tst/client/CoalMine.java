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

public class CoalMine {
	String host;
	public Server server;
	int citizenCap = 4;
	int numCitizens = 0;
	ExecutorService threadPool;
	public CoalMine(String hostname) {
//		if(System.getSecurityManager() == null) {
//			System.setSecurityManager(new SecurityManager());
//		}
		this.host = hostname;
		load(this.host);
		this.threadPool = Executors.newCachedThreadPool();
	}
	
	private boolean load(String hostname) {
		try {
			//Connection stuff
			String key = "Server";
			Registry reg = LocateRegistry.getRegistry(hostname);
			//
			server = (Server)reg.lookup(key);
			this.citizenCap = server.getMaxCitizens();
			this.numCitizens = server.getNumCitizens();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

	
	public synchronized boolean roomLeft() throws RemoteException {		
		try {
			this.numCitizens = this.server.getNumCitizens();
		} catch (RemoteException e) {
			// Try to reconnect.
			if(load(this.host)) {
				this.numCitizens = this.server.getNumCitizens();
			} else {
				throw new RemoteException("Can't connect to server.");
			}
		}
		
		if (this.numCitizens >= this.citizenCap) {
			return false;
		}
		return true;
	}
	
	public boolean sendToMine(Citizen cit, List<FoundDate> attestations) {
		Callable<Float> mineCart = MineCartFactory.build(cit, attestations, this.server);
		Future<Float> future = threadPool.submit(mineCart);
		cit.setFitnessFuture(future);
		return true;
	}
	
	/***
	 * This method returns a fitness score for a citizen and BLOCKS while doing so. 
	 * Use sendToMine() for the non blocking Future implementation.
	 * @param cit The citizen we want to send away to align things until it dies. And then we judge
	 * it. Hooray for equality!
	 * @param attestations The sample on which the citizen will work
	 * @return How well the Sequence Alignment Overlords determine the citizen to have lived.
	 */
	public float workLifeAway(Citizen cit, List<FoundDate> attestations) {
		try {
			//Callable mineCart = MineCartFactory.build(cit, attestations, this.server);
			// TODO: make this non blocking
			return server.live(cit, attestations);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return -1F;
	}
	//XXX
    public static final String dbHost = "jdbc:mysql://cuneiform.cs.wwu.edu/cuneiform";
    public static final String dbUser = "dingo";
    public static final String dbPass = "hungry!";
    
	public static void main(String[] args) {
		
		//CoalMine cm = new CoalMine("cf405-19.cs.wwu.edu");
		CoalMine cm = new CoalMine("cf416-15.cs.wwu.edu");
		Citizen cit = new Citizen();
		registerMySqlDriver();
		
		
		
		try {
			
			Connection conn = DriverManager.getConnection(dbHost, dbUser, dbPass);
			
			List<FoundDate> firstDate = (new FoundDateList(conn)).getFoundDates();
			
			float f = cm.server.live(cit, firstDate);
			
			//float g = belh.server.live(null, null);
			System.out.println("Running client on " + InetAddress.getLocalHost().getHostName());
			System.out.println("Float " + f + " recieved from server on:\n" +cm.server.getHostName());
			//System.out.println("Float " + g + " recieved from server on:\n" + belh.server.getHostName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//XXX
    private static void registerMySqlDriver()
    {
    	/*
    	 * 	Steps to register MySQL JDBC under Ubuntu:
    	 * 		1. sudo apt-get install libmysql-java
    	 * 		2. Ensure that /usr/share/java/mysql.jar exists.
    	 * 		3. Register external .jar with your IDE.
    	 * 			In Eclipse:
    	 * 			Project --> Properties --> Java Build Path
    	 * 				--> Add External JARs...
    	 */
    	try
    	{
    		// Register the MySQL JDBC driver.
    		
    		Class.forName("com.mysql.jdbc.Driver");
    	}
    	catch (ClassNotFoundException e)
    	{
    		// The MySQL Java connector does not appear to be installed.
    		// There's not much we can do about that !
    		
    		System.err.println("Failed to register the JDBC driver.");
    		e.printStackTrace();
    	}
    }
	
	
}
