package client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import cuneiform.Citizen;
import cuneiform.FoundDate;

public class LoadBalancer {
	/***
	 * List of the names (or addresses) of the nodes.
	 */
	String[] hostNames = new String[]{ };
	List<CoalMine> theMines;
	public LoadBalancer() {
		this.theMines = new ArrayList<CoalMine>(hostNames.length);
		generateMines(hostNames);
	}
	
	private void generateMines(String[] hosts) {
		for (String name : hosts) {
			this.theMines.add(new CoalMine(name));
		}
	}
	
	/***
	 * Send Citizen to a mine and then sets the fitness
	 * @param cit the citizen who will work their life away for us.
	 * @param attestations list of attestations bruh
	 * @return true if successful, false if all mines are full (that's pretty bad)
	 */
	public boolean sendToMine(Citizen cit, List<FoundDate> attestations) {
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
					 return curMine.sendToMine(cit, attestations);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			i = (i+1) % size; //loop through indices for theMines
			
		}
		
	} 
	
}
