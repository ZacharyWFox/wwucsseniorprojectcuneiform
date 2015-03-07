package cuneiform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import client.CoalMine;
import client.LoadBalancer;

public class Experiment {

	
	private ArrayList<Citizen> Population;
	private int GenerationNo;
	private Citizen bestCit;
	private int newCitNo = 0;
	private int populationMax;
	private CitizenPool allCitizens;
	private boolean Debug = false;
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
	
		Experiment blah = new Experiment(10); //TODO correct number		
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
		} catch (RemoteException e) {
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
		int creamOfCrop = 10;
		double mutantPercent = .2;
		BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));

		ArrayList<Citizen> newPop = new ArrayList<Citizen>();
		
		while (true){
			
			
			
			
			long starttime = System.nanoTime();
			
			if (Debug){
				String curPopStr = "Current Population: \n";
				
				for (int i = 0; i < Population.size(); i++){
					curPopStr += Population.get(i).toString();
				}
				System.out.println(curPopStr);
				System.out.println(allCitizens.getStats());
			}
			
			
			
			Live(Population);
			if (quitNow){
				break;
			}
			Collections.sort(Population);
			

			
			
			//Top 10 are immortal
			//rest of population for new gen is created from
			//crossovers of current gen (randomized over entire pop, lean towards top 10 for crossover, lean away from bottom) 
			//with small num having mutations			
			
			if( bestCit == null ||  Population.get(0).fitness > bestCit.fitness){
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
			
			while (newPop.size() < populationMax){
				//breed my citizens! mwahahaha
				//Note: we also allow hermaphrodites
				
				int probA = (int) Math.floor((Math.random() * totalFitness));
				int probB = (int) Math.floor((Math.random() * totalFitness));
				int AIndex = -1;
				int BIndex = -1;
				
				for ( int i = 0; i < Population.size(); i++){
					int curFitness = (int)Population.get(i).getFitness();
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
			
			GenerationNo++;
			long endtime = System.nanoTime();
			GenTimeHistory.add((endtime - starttime)/1000000000);
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
		//limit number of cells to mutate to max 1%
		//limit numbers in the similarity matrix to <= 127 and >= -127, since it's stored in bytes (largest num represent is 127)
		Citizen mutant = allCitizens.getCitizen(newCitNo); 
		mutant.personalMatrix = A.personalMatrix.clone();
		newCitNo++;
		
		if (Debug){
			System.out.println("Citizen A matrix before mutating: " + A.personalMatrix.toString());
		}
		
		int Xmax = A.personalMatrix.rowLength() - 1;
		int Ymax = A.personalMatrix.colLength() - 1;
		int loopMax = (int) (Math.floor(populationMax * .01));
		
		for (int i = 0; i < loopMax; i++){
			
			int x = (int) (Math.floor(Xmax * Math.random()));
			int y = (int) (Math.floor(Ymax * Math.random()));
			byte newVal = (byte) (Math.floor(127 * Math.random()));
			if (Math.random() > .5){
				newVal = (byte) -newVal;
			}
			
			try {
				mutant.personalMatrix.setCell(x, y, newVal);
			} catch (Exception e) {

				System.out.println("Something went horribly wrong (or there is an off by one) in mutate:" + e.getMessage());
				i--;
			}
		}
		
		if (Debug){
			System.out.println("Citizen A matrix: " + A.personalMatrix.toString());
			System.out.println("Mutant matrix: " + mutant.personalMatrix.toString());
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
					}
				}
				else if (coin <= .5){
					try {
						child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
					} catch (Exception e) {
						System.out.println("Something went wrong (coin <= .5) in crossover()" + e.getMessage());
					}
				}
				else{
					if (dealbreaker == 1){
						try {
							child.personalMatrix.setCell(i, j,  A.personalMatrix.getCell(i, j));
						} catch (Exception e) {
							System.out.println("Something went wrong (dealbreaker == 1) in crossover(). " + e.getMessage());
						}
						dealbreaker = 0;
					}
					else{
						try {
							child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
						} catch (Exception e) {
							System.out.println("Something went wrong (dealbreaker != 1) in crossover(). " + e.getMessage());
						}
						dealbreaker = 1;
					}
					
				}
				
			}
		}
		
		if (Debug){
			System.out.println("Parent A: " + A.personalMatrix.toString());
			System.out.println("Parent B: " + B.personalMatrix.toString());
			System.out.println("Child: " + child.personalMatrix.toString());
		}
		
		
		return child;
	}

	public void Live(ArrayList<Citizen> curGen){
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
		for (Citizen curCit : curGen){
			boolean ret = loadBalancer.sendToMine(curCit, nthDateIKnow);
			
			if (!ret){
				//something went wrong
			}
		}
		
			//output all current citizens to file (for catastrophic overload)
			//overlapping time that citzens need to calculate fitness
			long starttimes = System.nanoTime();
			try {
				File genFile = new File(outputGenList);
				
				if (!genFile.exists()){
					genFile.mkdir();
				}
				String path = genFile.getPath();
				for (int i= 0; i < Population.size(); i++){
					Population.get(i).personalMatrix.writeMatrix(path + "/Cit" + i + ".txt");
				}
				
			} catch (Exception e) {

				e.printStackTrace();
			}
			long endtimes = System.nanoTime();
			
			if (Debug){
				System.out.println("it took: " + (endtimes - starttimes) + "nanoseconds");
			}
			
		//now that they're there, wait for them to die
		for (Citizen curCit : curGen){
			boolean result = curCit.evaluateFitness();
			
			if (!result){
				//something went wrong

			}
			
			
			try {
				if (cin.ready()){
					String line = cin.readLine();
					if (line.toLowerCase().equalsIgnoreCase("quit")){
						quitNow = true;
					}
					if (line.toLowerCase().equalsIgnoreCase("status")){
						printStatus();
					}
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		//got them all
		return;
		
	}
	
	
	public void printStatus(){
		
		String genHist = "[";
		String genChange = "[";
		String genTime = "[";
		
		for (int i = 0; i < GenHistory.size(); i++){
			genHist += " " + GenHistory.get(i) + ",";
			
			if (i > 0){
				genChange += " " + (GenHistory.get(i) - GenHistory.get(i-1)) + ",";
			}
		}
		
		for (int i = 0; i < GenTimeHistory.size(); i++){
			genTime += " " + GenTimeHistory.get(i) + ",";
		}
		
		genHist += "]";
		genChange += "]";
		genTime += "]";
		
		System.out.println("Current gen is: " + GenerationNo);
		System.out.println("Best fitness for each Generation: " + genHist);
		System.out.println("Change in fitness between each Generation: " + genChange);
		System.out.print("Time to complete each Generation (in sec): " + genTime);
		
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(statusFilePath));
			out.append("Best fitness for each Generation: " + genHist);
			out.append("Change in fitness between each Generation: " + genChange);
			out.append("Time to complete for each Generation (in sec) " + genTime);
			out.append("\n\n");
			out.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		
		
		
	}
	
}
