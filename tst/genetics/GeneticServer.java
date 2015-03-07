package genetics;

import interfaces.Server;

import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;
import cuneiform.GuessPair;
import cuneiform.CallableDateExtractor;
import cuneiform.KnownDate;
import cuneiform.stringComparator.SimilarityMatrix;

public class GeneticServer implements Server {
	String name = "Default";
	String hostName;
	ExecutorService threads;
	int currentCitizens = 0;
	int capCitizens = 6;
	int threadsPerCitizen = 4;
	List<KnownDate> allKnownDates;
	
	public GeneticServer(int citizenCap, int threadsPerCit) {
		super();
		this.threadsPerCitizen = threadsPerCit;
		this.capCitizens = citizenCap;
		this.threads = Executors.newFixedThreadPool(24);
	}
	
	public void setAllKnownDates(List<KnownDate> dates) {
		this.allKnownDates = dates;
	}
	
	@Override
	public float live(Citizen cit, List<FoundDate> attestations)
			throws RemoteException {
		System.out.println("Life of citizen " + cit.IDNo + " has started.");
		//TODO: add timestamp at beginning and end and print it to a file.
		long timeStart = System.currentTimeMillis();
		incrementCitizen();
		//return 3.14159F;
		int divider = (int)Math.ceil(attestations.size()/threadsPerCitizen);
		List<List<FoundDate>> threadDivisions = new ArrayList<List<FoundDate>>(threadsPerCitizen);
		List<GuessPair> guesses = new ArrayList<GuessPair>(attestations.size());
		
		int min = 0;
		int max = 0;
		int leftover;
		
		//divide work
		// TODO: test
		for (int i = 0; i < threadsPerCitizen; ++i) {
			min = i * divider;
			max = min + divider;
			leftover = max - attestations.size();
			if(leftover > 0)
				max -= leftover;
			try {
				threadDivisions.add(attestations.subList(min, max));
				System.out.printf("Gave thread %d elements %d - %d.\n", i, min, max);
			} catch (Exception e) {
				System.out.println("Sublist failed.");
			}
		}
		
		// Separate
		//Start threads
		//

		
		List<Future<List<GuessPair>>> results = new ArrayList<Future<List<GuessPair>>>(this.threadsPerCitizen);
		// Start the work
		long threadSt = System.currentTimeMillis();
		for (List<FoundDate> f : threadDivisions){
			//List<KnownDate> known = toKnownDateList(f);
			
			results.add(threads.submit(new CallableDateExtractor(null, allKnownDates, f, cit.personalMatrix)));
		}
		
		//Join
		for (Future<List<GuessPair>> r : results) {
			try {
				guesses.addAll(r.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.printf("Citizen %d lived for %d milliseconds...RIP.\n", cit.IDNo, System.currentTimeMillis() - threadSt);
		// Get fitness
		float fitness = 0;
		try {
			fitness = evaluateFitness(guesses);
		} catch (Exception e) {
			// Let the crap flow uphill
			throw new RemoteException(e.getMessage(), e);
		}
		// Done!
		//TODO: needs synchronized access somehow
		decrementCitizen();
		System.out.println("Life of citizen " + cit.IDNo + " has ended.");
		return fitness;
	}
	
	private synchronized void incrementCitizen() {
		this.currentCitizens++;
	}
	private synchronized void decrementCitizen(){
		this.currentCitizens--;
	}
	
	private float evaluateFitness(List<GuessPair> guesses) throws Exception{
		if (guesses.isEmpty()) {
			System.out.println ("ERROR: Recieved empty list of guesses. That's bad.");
			throw new Exception("ERROR: Recieved empty list of guesses. That's bad.");
		}
		
		// Count the number correct
		int correct = 0;
		for(GuessPair g : guesses) {
			if(g.isMatch()) {
				correct++;
			}
		}
		
		//Return the ratio of correctness
		return Math.abs(correct/guesses.size());
	}
	
	private List<KnownDate> toKnownDateList(List<FoundDate> found) {
		List<KnownDate> known = new ArrayList<KnownDate>(found.size());
		for (FoundDate f : found) {
			known.add(f.date);
		}
		return known;
	}
	

	@Override
	public String getName() throws RemoteException {
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
	public String getIPAddressString() throws RemoteException {
		// TODO Auto-generated method stub
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public int getMaxCitizens() {
		// TODO Auto-generated method stub
		return this.capCitizens;
	}

	@Override
	public synchronized int getNumCitizens() {
		// TODO Auto-generated method stub
		return this.currentCitizens;
	}
	
	public static void main(String[] args) {
//		if (System.getSecurityManager() == null) {
//			System.setSecurityManager(new SecurityManager());
//		}
		
		try {
			
			String key = "Server";
			Server jenkins;
			if( args.length < 2) {
				jenkins = new GeneticServer(6, 4);
			} else {
				int cit = Integer.parseInt(args[0]);
				int thds = Integer.parseInt(args[1]);
				jenkins = new GeneticServer(cit, thds);
			}
			
			if (args.length > 2)
				jenkins.setName(args[2]);
			
			
			Server stub = (Server) UnicastRemoteObject.exportObject(jenkins, 0);
			
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind(key, stub);
		} catch (Exception e) {
			System.out.println("Failed to start GeneticServer");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Genetic Server bound.");
	}
}
