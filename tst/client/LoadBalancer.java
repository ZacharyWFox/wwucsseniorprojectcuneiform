package client;

import java.util.ArrayList;
import java.util.List;

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
	 * Send Citizen to a mine and then sets the 
	 * @param cit the citizen who will work their life away for us.
	 * @param attestations list of attestations bruh
	 * @return true if successful, false if all mines are full (that's pretty bad)
	 */
	public boolean sendToMine(Citizen cit, List<FoundDate> attestations) {
		// TODO: determine the mine
		CoalMine assignment = theMines.get(0); //TODO: get reference to open mine.
		
		// Citizen's future will be updated
		return assignment.sendToMine(cit, attestations);
	} 
	
}
