package cuneiform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Experiment {

	
	private ArrayList<Citizen> Population;
	private int GenerationNo;
	private Citizen bestCit;
	private ExecutorService threadPool;
	private int newCitNo = 0;
	private int populationMax;
	private CitizenPool allCitizens;
	private boolean Debug = true;
	private ArrayList<Integer> GenHistory;
	
	
	public static void main(String[] args){
		
		Experiment blah = new Experiment(10);		
		blah.runExperiment();
	}
	
	
	public Experiment(int populationNum) {
		// intialize stuff.
		populationMax = populationNum;
		Population = new ArrayList<Citizen>();
		allCitizens = new CitizenPool((populationNum *2) + 10);
		GenHistory = new ArrayList<Integer>();
		
		for (int i = 0; i < populationNum; i++){
			Citizen newCit = allCitizens.getCitizen(newCitNo);
			newCit.personalMatrix.randomizeMatrix();
			Population.add(newCit);
			newCitNo++;
		}
		GenerationNo = 1;
		threadPool = Executors.newFixedThreadPool(populationNum); 
	}
	
	 
	public void runExperiment(){
		int creamOfCrop = 3;
		double mutantPercent = .2;
		BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));

		ArrayList<Citizen> newPop = new ArrayList<Citizen>();
		
		while (true){
			
			if (Debug){
				String curPopStr = "Current Population: \n";
				
				for (int i = 0; i < Population.size(); i++){
					curPopStr += Population.get(i).toString();
				}
				System.out.println(curPopStr);
				System.out.println(allCitizens.getStats());
			}
			
			
			
			Live(Population);
			Collections.sort(Population);
			//Top 10 are immortal
			//rest of population for new gen is created from
			//crossovers of current gen (randomized over entire pop, lean towards top 10 for crossover, lean away from bottom) 
			//with small num having mutations			
			
			if( bestCit == null ||  Population.get(0).fitness > bestCit.fitness){
				bestCit = Population.get(0);
			}
			
			GenHistory.add(new Integer(Population.get(0).getFitness()));
			
			
			
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
					int curFitness = Population.get(i).getFitness();
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
		}
		
		System.out.println("The program has ended. Here are the results: ");
		
		System.out.println("Generation No: " + GenerationNo);
		System.out.println("Best citizen: " + bestCit.toString());
		System.out.println("The best similarity matrix: " + bestCit.personalMatrix.toString());

		
			
	}
	
	
	public Citizen Mutate(Citizen A){

		//Modify citizen's similarity matrix. 
		//limit number of cells to mutate to max 100
		//limit numbers in the similarity matrix to <= 127, since it's stored in bytes (largest num represent is 127)
		Citizen mutant = allCitizens.getCitizen(newCitNo); 
		mutant.personalMatrix = A.personalMatrix.clone();
		newCitNo++;
		
		if (Debug){
			System.out.println("Citizen A matrix before mutating: " + A.personalMatrix.toString());
		}
		
		int Xmax = A.personalMatrix.rowLength() - 1;
		int Ymax = A.personalMatrix.colLength() - 1;
		int loopMax = (int) (Math.floor(100 * Math.random()));
		
		for (int i = 0; i < loopMax; i++){
			
			int x = (int) (Math.floor(Xmax * Math.random()));
			int y = (int) (Math.floor(Ymax * Math.random()));
			int add = (int) (Math.floor(50 * Math.random()));
			try {
				mutant.personalMatrix.setCell(x, y, (byte)((A.personalMatrix.getCell(x, y) + add) % 127));
			} catch (Exception e) {
				//TODO: handle gracefully
				System.out.println("Something went horribly wrong (or there is an off by one):" + e.getMessage());
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
						System.out.println("Something went wrong (coin > .5)" + e.getMessage());
					}
				}
				else if (coin <= .5){
					try {
						child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
					} catch (Exception e) {
						System.out.println("Something went wrong (coin <= .5)" + e.getMessage());
					}
				}
				else{
					if (dealbreaker == 1){
						try {
							child.personalMatrix.setCell(i, j,  A.personalMatrix.getCell(i, j));
						} catch (Exception e) {
							System.out.println("Something went wrong (dealbreaker == 1). " + e.getMessage());
						}
						dealbreaker = 0;
					}
					else{
						try {
							child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
						} catch (Exception e) {
							System.out.println("Something went wrong (dealbreaker != 1). " + e.getMessage());
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
		//Spawn thread for each citizen.
		//in that thread, have the citizen run the needleman wunsch algo with their unique
		//similarity matrix. Based off of how good they do against the data, assign them
		//a fitness score. TODO: check out this warning
		ArrayList<Future> futureCit = new ArrayList<Future>();
		
		//start the threads!
		for (int i = 0; i < curGen.size(); i++){
			futureCit.add(threadPool.submit(curGen.get(i)));
		}
		
		
		//TODO deal with exceptions somehow? (stick them in the thread pool again, just record?)
		//now that each person is doing their thing,
		//we have to wait for all of them to finish.
		for (int i = 0; i < futureCit.size(); i++){
			try {
				futureCit.get(i).get();
				
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	
	public void printStatus(){
		
		String genHist = "[";
		String genChange = "[";
		
		for (int i = 0; i < GenHistory.size(); i++){
			genHist += " " + GenHistory.get(i) + ",";
			if (i > 0){
				genChange += " " + (GenHistory.get(i) - GenHistory.get(i-1)) + ",";
			}
		}
		
		System.out.println("Best fitness for each Generation: " + genHist);
		System.out.println("Change in fitness between each Generation: " + genChange);
		
		
		
	}
	
}
