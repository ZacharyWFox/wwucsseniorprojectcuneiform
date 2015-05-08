package genetics;

import interfaces.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
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
import java.util.Date;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.FoundDate;
import cuneiform.GuessPair;
import cuneiform.CallableDateExtractor;
import cuneiform.KnownDate;
import cuneiform.stringComparator.SimilarityMatrix;

/**
 * A work server instance, this is where the computation is being run. 
 * Runs a bunch of Citizens and DateExtractors in parallel, calculates fitness, 
 * and returns result. Run one on each node being utilized. 
 * Main is invoked usually with script etc/startNode.py
 * @author ZacharyWFox
 * @author tcfritchman
 * @author DThurow
 * check blame for specific authorship
 */
public class GeneticServer implements Server {
	String name = "Default";
	String hostName;
	ExecutorService threads;
	volatile int currentCitizens = 0;
	int capCitizens = 12;
	int threadsPerCitizen = 2;
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
	
	private static void redirectOutputToFile(String hostname) {
		// Provides simple and easy logging of output from the experiment.
		
		String outFilename;
		String errFilename;
		String dirName = "Logs";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());;

		try {
			File logDir = new File(dirName);
			if (!logDir.isDirectory()) {
				logDir.mkdirs();
			}
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		// Example: Logs/2015-03-10-13-45-00_compute-0-0_err.log
		outFilename = dirName + "/" + timeStamp + "_" + hostname + "_out.log";
		errFilename = dirName + "/" + timeStamp + "_" + hostname + "_err.log";

		try {
			System.setOut(new PrintStream(new File(outFilename)));
			System.setErr(new PrintStream(new File(errFilename)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	
	
	@Override
	public float live(Citizen cit, List<FoundDate> attestations)
			throws RemoteException {
			return live(cit, attestations, false);
	}
		
	// Where the work is done.	
	public float live(Citizen cit, List<FoundDate> attestations, boolean compare)
			throws RemoteException {
		incrementCitizen();
		
		System.out.println("Life of citizen " + cit.IDNo + " has started. Aligning " + attestations.size() + " attestations");
		long timeStart = System.currentTimeMillis();
		
		int divider = (int)Math.ceil(attestations.size()/threadsPerCitizen);
		List<List<FoundDate>> threadDivisions = new ArrayList<List<FoundDate>>(threadsPerCitizen);
		List<GuessPair> guesses = new ArrayList<GuessPair>(attestations.size());
		
		int min = 0;
		int max = 0;
		int leftover;
		
		//divide work
		for (int i = 0; i < threadsPerCitizen; ++i) {
			min = i * divider;
			max = min + divider;
			leftover = max - attestations.size();
			if(leftover > 0)
				max -= leftover;
			try {
				threadDivisions.add(attestations.subList(min, max));
				System.out.println("Divided sbulist. Min = " + min + " max = " + max);
			} catch (Exception e) {
				System.out.println("Sublist failed.");
			}
		}
		
		List<Future<List<GuessPair>>> results = new ArrayList<Future<List<GuessPair>>>(this.threadsPerCitizen);
		// Start the work
		long threadSt = System.currentTimeMillis();
		for (List<FoundDate> f : threadDivisions){
			results.add(threads.submit(new CallableDateExtractor(null, allKnownDates, f, cit.personalMatrix, compare)));
			System.out.println("submitted " + f.size() + " attestations to thread.");
		}
		
		//Join
		System.out.println("We have " + results.size() + " sets of results. combining togther.");
		for (Future<List<GuessPair>> r : results) {
			try {
				float tmillis = System.currentTimeMillis();
				List<GuessPair> sublist = r.get();
				System.out.println("Waited for " + (System.currentTimeMillis() - tmillis));
				guesses.addAll(sublist);
				System.out.println("Got a thing. Guesses size = " + guesses.size() + ", added " + sublist.size());
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
		float fitness = -1;
		try {
			fitness = evaluateFitness(guesses);
		} catch (Exception e) {
			// Let the exceptions go up the stack
			throw new RemoteException(e.getMessage(), e);
		}
		
		System.out.println("Life of citizen " + cit.IDNo + " has ended. Fitness: " + fitness);
		decrementCitizen();
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
		float totalConf = 0F;
		int correctGuess = 0;
		for(GuessPair g : guesses) {
			if (g.getGuessDate().text.equals(g.getCorrectDate().text)){
				correctGuess++;
				correct += 2 * (g.getConfidence().confidence/100);
//				System.out.println("Found match! Confidence of " + g.getConfidence().confidence 
//						+ " for alignment of " + g.getGuessDate().text + " against "+ g.getCorrectDate().text);
//				System.out.println("Correct = " + correct + " calculation: " 
//						+ (g.getConfidence().confidence/100));
			}
			totalConf += g.getConfidence().confidence;
			
		}
		System.out.println("Average confidence: " + totalConf/guesses.size() + " over " + guesses.size() + " guesses.");
		System.out.println("Correct guesses: " + correctGuess);
		//Return the ratio of correctness
		return correct;
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
		
		String hostname = "default";
		if (args.length > 0) {
			hostname = args[0];
			redirectOutputToFile(hostname);
		} else {
			redirectOutputToFile(hostname);
		}
		
		try {
			
			String key = "Server";
			Server jenkins;
			
			
//			if( args.length < 2) {
			
			jenkins = new GeneticServer(12, 2);
			
//			}/* else {
//				int cit = Integer.parseInt(args[0]);
//				int thds = Integer.parseInt(args[1]);
//				jenkins = new GeneticServer(cit, thds);
//			}*/
			
//			if (args.length > 2)
//				jenkins.setName(args[2]);
			
			Server stub = (Server) UnicastRemoteObject.exportObject(jenkins, 0);
			
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind(key, stub);
		} catch (Exception e) {
			System.out.println("Failed to start GeneticServer");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Genetic Server bound.");
	}
	
}
