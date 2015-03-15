package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;
import cuneiform.KnownDate;

public class LoadBalancer {
	/***
	 * List of the names (or addresses) of the nodes.
	 *///TODO: test connections to these names
	String[] hostNames = new String[]{
			"compute-0-0", 
			"compute-0-1", 
			"compute-0-2", 
			"compute-0-3",
			"compute-0-4", 
			"compute-0-5", 
			"compute-0-6", 
			"compute-0-7",
			"compute-0-8", 
			"compute-0-9"//, 
//			"compute-0-10", 
//			"compute-0-11",
//			"compute-0-12", 
//			"compute-0-13", 
//			"compute-0-14", 
//			"compute-0-15", 
//			"compute-0-16"
			};
	//XXX
	String[] testNames = new String[]{
		"cf405-13",
		"cf405-16"
	};
	List<CoalMine> theMines;
	public List<MockCoalMine> fakeMines;
	public LoadBalancer(List<KnownDate> allKnownDates) throws RemoteException, NotBoundException{
		this.theMines = new ArrayList<CoalMine>(hostNames.length);
		//this.fakeMines = new ArrayList<MockCoalMine>(hostNames.length);
		
		generateMines(hostNames, allKnownDates);
	}
	
	private void generateMines(String[] hosts, List<KnownDate> allKnownDates) throws RemoteException, NotBoundException{
		for (String name : hosts) {
			this.theMines.add(new CoalMine(name, allKnownDates));
			//this.fakeMines.add(new MockCoalMine(name));
		}
	}
	
	/***
	 * Send Citizen to a mine and then sets the fitness
	 * @param cit the citizen who will work their life away for us.
	 * @param attestations list of attestations bruh
	 * @return true if successful, false if all mines are full (that's pretty bad)
	 * @throws NotBoundException 
	 */
	public boolean sendToMine(Citizen cit, List<FoundDate> attestations) throws NotBoundException {
		//randomize starting index means less likely to hit full one right off the bat
		int size = theMines.size();
		int startIndex = (int) Math.floor(Math.random() * size);
		int i = (startIndex+1) % size;
		
		
		while (true){
			if (i == startIndex){
				//looped through the whole thing, nobody's available!
				return false;
			}
			CoalMine curMine = theMines.get(i);
			try {
				if (curMine.roomLeft()){
					System.out.println("Sent " + cit + " to mine "+ curMine.host);
					return curMine.sendToMine(cit, attestations);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			i = (i+1) % size; //loop through indices for theMines
			
		}
		
	} 
	
	public void resetGen() {
		for (CoalMine cm : this.theMines) {
			cm.resetMineCarts();
		}
	}
	
	public boolean isAllDone() {
		for(CoalMine cm: this.theMines) {
			if (!cm.isDone()) {
				return false;
			}
		}
		return true;
	}
	
}
