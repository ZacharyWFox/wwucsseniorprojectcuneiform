package client;

import interfaces.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cuneiform.Citizen;
import cuneiform.DateExtractor;
import cuneiform.FoundDate;
import cuneiform.FoundDateList;
import cuneiform.KnownDate;

/**
 * This represents a connection to a work server. Several MineCarts (A slot in the work server that tracks progress)
 * are tracked and sent off to servers to "work" from here.
 * @author ZacharyWFox
 */
public class CoalMine {
	String host;
	public Server server;
	int citizenCap = 12;
	int numCitizens = 0;
	ExecutorService threadPool;
	List<MineCart> mineCarts;
	public CoalMine(String hostname, List<KnownDate> known) throws RemoteException, NotBoundException {
		this.host = hostname;
		load(this.host);
		this.threadPool = Executors.newCachedThreadPool();
		this.server.setAllKnownDates(known);
		this.mineCarts = new ArrayList<MineCart>(citizenCap);
	}
	
	public void resetMineCarts(){
		this.mineCarts.clear();
	}
	
	private boolean load(String hostname) throws RemoteException, NotBoundException {

		//Connection stuff
		String key = "Server";
		Registry reg = LocateRegistry.getRegistry(hostname);
		
		server = (Server)reg.lookup(key);
		this.citizenCap = server.getMaxCitizens();
		this.numCitizens = server.getNumCitizens();

		return true;
	}
	
	public boolean isDone() {
		for (MineCart mc : this.mineCarts) {
			if (!mc.isDone()) {
				return false;
			}
			//System.out.println("Citizen " + mc.cit.IDNo + " is done.");
		}
		return true;
	}
	
	public synchronized boolean roomLeft() throws RemoteException, NotBoundException {		
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
	
	// Send Citizens off to server via a MineCart
	public boolean sendToMine(Citizen cit, List<FoundDate> attestations) {
		
		MineCart mc = new MineCart(cit, attestations, this.server);
		mineCarts.add(mc);
		Future f = this.threadPool.submit(mc);
		return true;
	}
	
	public void join() {
		
	}
	
	
}
