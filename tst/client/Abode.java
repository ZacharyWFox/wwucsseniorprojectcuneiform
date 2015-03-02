package client;

import genetics.GeneticServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;

public class Abode {
	String host;
	GeneticServer server;
	public Abode(String hostname) {
		if(System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			//Connection stuff
			String key = "Server";
			Registry reg = LocateRegistry.getRegistry(hostname);
			//
			GeneticServer server = (GeneticServer)reg.lookup(key);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public float timeOfYourLife(Citizen cit, List<FoundDate> attestations) {
		try {
			return server.live(cit, attestations);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return -1F;
	}
}
