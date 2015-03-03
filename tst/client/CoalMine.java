package client;

import genetics.GeneticServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;

public class CoalMine {
	String host;
	GeneticServer server;
	
	int citizenCap = 4;
	int numCitizens = 0;
	public CoalMine(String hostname) {
		if(System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			//Connection stuff
			String key = "Server";
			Registry reg = LocateRegistry.getRegistry(hostname);
			//
			GeneticServer server = (GeneticServer)reg.lookup(key);
			this.citizenCap = server.getMaxCitizens();
			this.numCitizens = server.getNumCitizens();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean roomLeft() {
		if (this.numCitizens >= this.citizenCap) {
			return false;
		}
		this.numCitizens++;
		return true;
	}
	
	public float workLifeAway(Citizen cit, List<FoundDate> attestations) {
		try {
			return server.live(cit, attestations);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return -1F;
	}
}
