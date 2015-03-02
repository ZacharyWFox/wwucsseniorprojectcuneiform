package genetics;

import interfaces.Server;

import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
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

public class GeneticServer implements Server {
	String name = "Default";
	String hostName;
	ExecutorService threads;
	int currentCitizens = 0;
	int capCitizens = 6;
	int threadsPerCitizen = 4;
	
	public GeneticServer(int citizenCap, int threadsPerCit) {
		super();
		this.threadsPerCitizen = threadsPerCit;
		this.capCitizens = citizenCap;
		this.threads = Executors.newFixedThreadPool(24);
		//TODO: initialize threads
	}
	
	
	
	
	@Override
	public float live(Citizen cit, List<FoundDate> attestations)
			throws RemoteException {
		// TODO Auto-generated method stub
		int divider = (int)Math.ceil(attestations.size()/threadsPerCitizen);
		List<List<FoundDate>> threadDivisions = new ArrayList<List<FoundDate>>(threadsPerCitizen);
		List<GuessPair> guesses = new ArrayList<GuessPair>(attestations.size());
		
		int min;
		int max = -1;
		int leftover;
		
		//divide work
		for (int i = 0; i < threadsPerCitizen; ++i) {
			min = i * divider;
			max = min + divider - 1;
			leftover = max - (attestations.size() - 1) + 2;
			if(leftover > 0)
				max -= leftover;
			try {
				threadDivisions.add(attestations.subList(min, max));
			} catch (Exception e) {
				System.out.println("Sublist failed.");
			}
		}
		
		// Separate
		//Start threads
				
		this.currentCitizens++;
		
		List<Future<List<GuessPair>>> results = new ArrayList<Future<List<GuessPair>>>(this.threadsPerCitizen);
		
		for (List<FoundDate> f : threadDivisions){
			List<KnownDate> known = toKnownDateList(f);
			
			results.add(threads.submit(new CallableDateExtractor(null, known)));
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
		// Get fitness
		float fitness = 0;
		try {
			fitness = evaluateFitness(guesses);
		} catch (Exception e) {
			// Let the crap flow uphill
			throw new RemoteException(e.getMessage(), e);
		}
		// Done!
		
		return fitness;
	}
	
	public float evaluateFitness(List<GuessPair> guesses) throws Exception{
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
	
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
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
