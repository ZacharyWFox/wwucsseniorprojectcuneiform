package cuneiform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Date;

import cuneiform.stringComparator.SimilarityMatrix;

import client.CoalMine;
import client.LoadBalancer;

public class Experiment {

	
	private ArrayList<Citizen> Population;
	private int GenerationNo;
	private Citizen bestCit;
	private int newCitNo = 0;
	private int populationMax;
	private CitizenPool allCitizens;
	private static boolean Debug = true;
	private ArrayList<Float> GenHistory;
	private ArrayList<Long> GenTimeHistory;
	private LoadBalancer loadBalancer;
	private FoundDateList foundDateList;
	private String outputGenList = "data/CurGen";
	private String statusFilePath = "data/status.txt";
	private String outfileName = "data/finalMatrix.txt";
	private Connection dbConn;
	private boolean quitNow = false;
	
	
	public static void main(String[] args){
		
		if (args.length > 0){
			for (String s : args){
				if (s.toLowerCase().contains("debug")){
					Debug = true;
				}
				if (s.toLowerCase().contains("realrun")){
					Debug = false;
				}
			}
			
		}
		
		if (Debug){
			System.out.println("Debug has begun...");
		}
		
		redirectOutputToFile("Experiment");
//		Experiment blah = new Experiment(15); //TODO correct number	
		Experiment blah = new Experiment(100);
		blah.runExperiment();
	}
	
	
	public Experiment(int populationNum) {
		// intialize stuff.
		populationMax = populationNum;
		Population = new ArrayList<Citizen>();
		allCitizens = new CitizenPool((populationNum *2) + 10);
		GenHistory = new ArrayList<Float>();
		
		GenTimeHistory = new ArrayList<Long>();
		//get the database connection
		List<KnownDate> allKnownDates = null;
		try {
			registerMySqlDriver();
			dbConn = DriverManager.getConnection(Parser.dbHost, Parser.dbUser, Parser.dbPass);
			System.out.println("DB connection secured.");
			allKnownDates = DateExtractor.readKnownYears(dbConn);
			System.out.println("KnownYears read.");
		} catch (SQLException e) {
			System.out.println("couldn't connect to database. Dying.");
			e.printStackTrace();
			 System.exit(-1);
		}
		
		try {
			foundDateList = new FoundDateList(dbConn);
		} catch (SQLException e) {
			System.out.println("tried to get found dates, failed miserably. Dying.");
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			loadBalancer = new LoadBalancer(allKnownDates);
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Couldn't start load Balancer. Taking arrow to face.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		for (int i = 0; i < populationNum; i++){
			Citizen newCit = allCitizens.getCitizen(newCitNo);
			newCit.personalMatrix.randomizeMatrix();
			Population.add(newCit);
			newCitNo++;
		}
		GenerationNo = 1; 
	}
	
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
	
	public void runExperiment(){
		int creamOfCrop = (int) Math.round(this.populationMax * .1);
		double mutantPercent = .2;
		BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));

		ArrayList<Citizen> newPop = new ArrayList<Citizen>();
		
		while (true){
			
			
			
			
			long starttime = System.nanoTime();
			
			if (Debug){
				System.out.println("current alphabet size is: " + Population.get(0).personalMatrix.colLength());
			}
			
			
			
			try {
				Live(Population);
			} catch (NotBoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(-1);
			}
			if (quitNow){
				break;
			}
			Collections.sort(Population);
			

			
			
			//Top 10 are immortal
			//rest of population for new gen is created from
			//crossovers of current gen (randomized over entire pop, lean towards top 10 for crossover, lean away from bottom) 
			//with small num having mutations			
			
			if( bestCit == null ||  Population.get(0).getFitness() > bestCit.getFitness()){
				bestCit = Population.get(0);
			}
			
			// convert to float, fitness needs more resolution than 100.
			GenHistory.add(new Float((int)Population.get(0).getFitness()));
			
			
			
			for (int i = 0; i < creamOfCrop; i++){
				newPop.add(Population.get(i));
			}
			
			int totalMutants = (int) Math.floor(mutantPercent * populationMax);
			
			
			//mutate old members of population
			for (int i = 0; i< totalMutants; i++){
				int index = (int) Math.floor(Math.random() * Population.size());
				newPop.add(Mutate(Population.get(index)));
				
				
			}
			
			
			
			
			//crossovers!
			
			int totalFitness = 0;
			for (int i = 0; i < Population.size(); i++){
				totalFitness += Population.get(i).getFitness();
			}
			int probARemember;
			int probBRemember;
			while (newPop.size() < populationMax){
				//breed my citizens! mwahahaha <--- pervert.
				//Note: we also allow hermaphrodites
				
				int probA = (int) Math.floor((Math.random() * (totalFitness+1)));
				int probB = (int) Math.floor((Math.random() * (totalFitness+1)));

				probARemember = probA;
				probBRemember = probB;

				int AIndex = -1;
				int BIndex = -1;
				int firstZeroIndex = -1;
				for ( int i = 0; i < Population.size(); i++){
					int curFitness = (int)Population.get(i).getFitness();
					
					if (curFitness == 0 && firstZeroIndex < 0){
						firstZeroIndex = i;
					}
					if (curFitness < 0){
						System.out.println("Current fitness in Experiment is:" + curFitness);
						System.out.println("This is at " + i + "in the sorted population");
						System.exit(-1);
					}
					
					if (probA < curFitness && AIndex < 0){
						AIndex = i;
					}
					if (probB < curFitness && BIndex < 0){
						BIndex = i;
					}
					
					
					probA -= curFitness;
					probB -= curFitness;
					if (AIndex >= 0 && BIndex >=0){
						break;
					}
					//zero fitness, have to go to other setup
					if (probA == 0 && probB == 0 && firstZeroIndex > 0){
						break;
					}
					
				}
				
				
				//if we manage to get through the whole thing
				//without assigning a value they're zero fitnesses
				if (AIndex == -1){
					if (firstZeroIndex > -1){
						int var = (int) Math.floor(Math.random() * (Population.size() - firstZeroIndex));
						AIndex = firstZeroIndex + var;
					} else {
						// Make sure we're not getting out of range indeces, seriously.
						for (int i = 0; AIndex < 0 || AIndex >= Population.size(); ++i) {
							if ( i > 10) {
								AIndex = 0;
								break;
							}
							AIndex = (int) Math.floor(Math.random() * Population.size());
						}
					}
				}
				if (BIndex == -1){
					if (firstZeroIndex > -1){
						int var = (int) Math.floor(Math.random() * (Population.size() - firstZeroIndex));
						BIndex = firstZeroIndex + var;
					} else {
						// Make sure we're not getting out of range indeces, seriously.
						for (int i = 0; BIndex < 0 || BIndex >= Population.size(); ++i) {
							if (i > 10){
								BIndex = 0;
								break;
							}
							BIndex = (int) Math.floor(Math.random() * Population.size());
						}
					}
				}
				if (Debug){
					System.out.println("Aindex: " + AIndex + " BIndex: " + BIndex );
					if (AIndex <= 0  || BIndex <= 0){
						System.out.println("firstZeroIndex: " + firstZeroIndex + " Totalfitness:" + totalFitness );
						System.out.println("prob for A: " + probARemember + " prob for B: " + probBRemember);
					}
				}
				
				newPop.add(Crossover(Population.get(AIndex), Population.get(BIndex)));
			}
			
			
			
			
			//have the new population. Kill the normals
			for (int i = creamOfCrop; i < Population.size(); i++){
				allCitizens.killCitizen(Population.get(i));
			}
			
			Population.clear();
			Population = new ArrayList<Citizen>(newPop);
			newPop.clear();
			
			try {
				if (cin.ready()){
					String line = cin.readLine();
					if (line.toLowerCase().equalsIgnoreCase("quit")){
						break;
					}
					if (line.toLowerCase().equalsIgnoreCase("status")){
						printStatus();
					}
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			
			long endtime = System.nanoTime();
			GenTimeHistory.add((endtime - starttime)/1000000000);
			printStatus();
			
			GenerationNo++;
			if (Debug){
				System.out.println("The old generation is complete, and the new generation is chosen...");
			}
		}
		
		System.out.println("The program has ended. Here are the results: ");
		
		System.out.println("Generation No: " + GenerationNo);
		System.out.println("Best citizen: " + bestCit.toString());
		
		try {
			bestCit.personalMatrix.writeMatrix(outfileName);
		} catch (Exception e) {
			System.out.println("couldn't write to file...Gonna have to print out here... it's gonna be big.\n");
			System.out.println(bestCit.personalMatrix.toString().replace("{", " ").replace("}", " "));
			
			e.printStackTrace();
		}
		System.out.println("The best similarity matrix is written to: " + outfileName);
		
		

		
			
	}
	
	
	public Citizen Mutate(Citizen A){

		//Modify citizen's similarity matrix. 
		//limit number of cells to mutate to max 1% of all cells
		//limit numbers in the similarity matrix to <= 127 and >= -127, since it's stored in bytes (largest num represent is 127)
		Citizen mutant = allCitizens.getCitizen(newCitNo); 
		mutant.personalMatrix = A.personalMatrix.clone();
		newCitNo++;
		
		if (Debug){
			//System.out.println("Will be randomizing with max mutate index as: " + (A.personalMatrix.colLength() -1) );
		}
		
		int Xmax = A.personalMatrix.rowLength();
		int Ymax = A.personalMatrix.colLength();
		int loopMax = (int) (Math.floor((A.personalMatrix.colLength()* A.personalMatrix.rowLength()) * .01));
		
		for (int i = 0; i < loopMax; i++){
			
			int x = (int) (Math.floor(Xmax * Math.random()));
			int y = (int) (Math.floor(Ymax * Math.random()));
			byte newVal = (byte) (Math.floor((SimilarityMatrix.maxValue - SimilarityMatrix.minValue) * Math.random()));
			newVal += SimilarityMatrix.minValue;
//			if (Math.random() > .5){
//				newVal = (byte) -newVal;
//			}
			if (Debug){
				//System.out.println("x is " + x);
				//System.out.println("y is: " + y);
			}
			try {
				mutant.personalMatrix.setCell(x, y, newVal);
			} catch (Exception e) {

				System.out.println("Something went horribly wrong (or there is an off by one) in mutate:" + e.getMessage());
				e.printStackTrace();
				i--;
			}
		}
		

		
		
		return mutant;
	}
	
	public Citizen Crossover(Citizen A, Citizen B){
		
		//Create new citizen, for each place in matrix,
		//coin flip to take from parent A or B
		Citizen child = allCitizens.getCitizen(newCitNo);
		newCitNo++;
		
		int dealbreaker = 0;
		
		
		for(int i = 0; i < child.personalMatrix.rowLength(); i++){
			for (int j = 0; j < child.personalMatrix.colLength(); j++){
				double coin = Math.random();
				
				if (coin > .5){
					try {
						child.personalMatrix.setCell(i, j,  A.personalMatrix.getCell(i, j));
					} catch (Exception e) {
						System.out.println("Something went wrong (coin > .5) in crossover()" + e.getMessage());
						e.printStackTrace();
					}
				}
				else if (coin <= .5){
					try {
						child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
					} catch (Exception e) {
						System.out.println("Something went wrong (coin <= .5) in crossover()" + e.getMessage());
						e.printStackTrace();
					}
				}
				else{
					if (dealbreaker == 1){
						try {
							child.personalMatrix.setCell(i, j,  A.personalMatrix.getCell(i, j));
						} catch (Exception e) {
							System.out.println("Something went wrong (dealbreaker == 1) in crossover(). " + e.getMessage());
							e.printStackTrace();
						}
						dealbreaker = 0;
					}
					else{
						try {
							child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
						} catch (Exception e) {
							System.out.println("Something went wrong (dealbreaker != 1) in crossover(). " + e.getMessage());
							e.printStackTrace();
						}
						dealbreaker = 1;
					}
					
				}
				
			}
		}
		
		
		
		return child;
	}

	public void Live(ArrayList<Citizen> curGen) throws NotBoundException{
		BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));
		List<FoundDate> nthDateIKnow;
		try {
			if(dbConn.isClosed()) {
				dbConn = DriverManager.getConnection(Parser.dbHost, Parser.dbUser, Parser.dbPass);
			}
			
			nthDateIKnow = (new FoundDateList(dbConn)).getFoundDates();
		} catch (SQLException e) {
			try {
				dbConn = DriverManager.getConnection(Parser.dbHost, Parser.dbUser, Parser.dbPass);
				nthDateIKnow = (new FoundDateList(dbConn)).getFoundDates();
			} catch (SQLException e1) {
				System.out.println("Tried to reconnect to database and failed. *Dies of preventable illness*");
				e1.printStackTrace();
				this.quitNow = true;
				return;
			}			
		}
		
		
		//send them all to the mines!
		System.out.println("The dwarves are digging too deep and too greedily: start of Generation " + this.GenerationNo);
		for (Citizen curCit : curGen){
			//System.out.println("Sent citizen " + curCit.IDNo + " to the mines.");
			boolean ret = loadBalancer.sendToMine(curCit, nthDateIKnow);
	
			if (!ret){
				//something went wrong
				if (Debug){
					System.out.println("Couldn't send a citizen to the mines");
				}
			}
		}
		System.out.println("Citizens deployed. I hear drums...drums in the deep.");
		
		//output top ten citizens to file (for catastrophic overload)
		//overlapping time that citzens need to calculate fitness
		long starttimes = System.nanoTime();
		try {
			File genFile = new File(outputGenList);
			
			if (!genFile.exists()){
				genFile.mkdir();
			}
			String path = genFile.getPath();
			for (int i= 0; i < 10; i++){
				Population.get(i).personalMatrix.writeMatrix(path + "/Cit" + i + ".txt");
			}
			
		} catch (Exception e) {
			System.out.println("tried to access: " + Paths.get("").toAbsolutePath().toFile().getAbsolutePath() + outputGenList);
			e.printStackTrace();
		}
		long endtimes = System.nanoTime();
		
		if (Debug){
			System.out.println("it took: " + (endtimes - starttimes) + "nanoseconds to print cits to file");
		}
			
		// Now that they're there, wait for them to die
		while(!this.loadBalancer.isAllDone()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.loadBalancer.resetGen();
			
		
		for (Citizen curCit : curGen){
			
			float result = curCit.getFitness();
			
			if (Debug){
				System.out.println("Citizen " + curCit.IDNo + " came back with fitness " + result);
			}

			
			
		}
		System.out.println("End of Generation " + this.GenerationNo);
		
		//got them all
		return;
		
	}
	
	
	public void printStatus(){
		if (Debug){
			System.out.println("printing status");
		}
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(statusFilePath, true));
			if (GenerationNo > 1){
			out.append("Gen:" + GenerationNo + 
					" topFit:" + GenHistory.get(GenHistory.size() - 1) +
					" fitChange:" + (GenHistory.get(GenHistory.size() - 2) - GenHistory.get(GenHistory.size() - 1)  ) +
					   " time:" + GenTimeHistory.get(GenTimeHistory.size() - 1)  );
			}
			else{
				out.append("Gen:" + GenerationNo + 
						" topFit:" + GenHistory.get(GenHistory.size() - 1) +
						" fitChange:" + GenHistory.get(GenHistory.size() - 1) +
						   " time:" + GenTimeHistory.get(GenTimeHistory.size() - 1)  );
			}
			out.append("\n");
			out.close();
		} catch (IOException e) {
			System.err.println("An error has occured when printing status. Message: " + e.getMessage());
			e.printStackTrace();
		}
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
	
}
