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
	
	public static void main(String[] args){
		
		Experiment blah = new Experiment(10);		
		blah.runExperiment();
	}
	
	
	public Experiment(int populationNum) {
		// intialize stuff.
		populationMax = populationNum;
		Population = new ArrayList<Citizen>();
		allCitizens = new CitizenPool((populationNum *2) + 10);
		
		
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
		BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));

		ArrayList<Citizen> newPop = new ArrayList<Citizen>();
		
		while (true){
			
			Live(Population);
			Collections.sort(Population);
			//Top 10 are immortal
			//rest of population for new gen is created from
			//crossovers of current gen (randomized over entire pop, lean towards top 10 for crossover, lean away from bottom) 
			//with small num having mutations			
			
			if( bestCit == null ||  Population.get(0).Fitness > bestCit.Fitness){
				bestCit = Population.get(0);
			}
			
			for (int i = 0; i < creamOfCrop; i++){
				newPop.add(Population.get(i));
			}
			
			while (newPop.size() < populationMax){
				//breed my citizens! mwahahaha
				//Note: we also allow hermaphrodites
				//TODO: need to lean toward better fit citizens (currently choose uniformly in population)
				
				int AIndex = (int) Math.floor((Math.random() * Population.size()));
				int BIndex = (int) Math.floor((Math.random() * Population.size()));
				
				newPop.add(Crossover(Population.get(AIndex), Population.get(BIndex)));
				
				
			}
			
			//mutate all normals' children (don't touch top citizens)
			for (int i = creamOfCrop; i< newPop.size(); i++){
				double becomeMutant = Math.random();
				
				if (becomeMutant > 1){
					//5% chance someone is becoming mutant
					newPop.set(i, Mutate(newPop.get(i)));
				}
				
			}
			
			
			//have the new population. Kill the normals
			for (int i = creamOfCrop; i < Population.size(); i++){
				allCitizens.killCitizen(Population.get(i));
			}
			
			Population = newPop;
			
			
			try {
				if (cin.ready()){
					String line = cin.readLine();
					if (line.toLowerCase().equalsIgnoreCase("quit")){
						break;
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
		
		
		int Xmax = A.personalMatrix.rowLength();
		int Ymax = A.personalMatrix.colLength();
		int loopMax = (int) (Math.floor(100 * Math.random()));
		
		for (int i = 0; i < loopMax; i++){
			
			int x = (int) (Math.floor(Xmax * Math.random()));
			int y = (int) (Math.floor(Ymax * Math.random()));
			int add = (int) (Math.floor(50 * Math.random()));
			
			mutant.personalMatrix.setCell(x, y, (A.personalMatrix.getCell(x, y) + add) % 127);
			
		}
		
		System.out.println("Citizen A matrix: " + A.personalMatrix.toString());
		System.out.println("Mutant matrix: " + mutant.personalMatrix.toString());
		
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
					child.personalMatrix.setCell(i, j,  A.personalMatrix.getCell(i, j));
				}
				else if (coin < .5){
					child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
				}
				else{
					if (dealbreaker == 1){
						child.personalMatrix.setCell(i, j,  A.personalMatrix.getCell(i, j));
						dealbreaker = 0;
					}
					else{
						child.personalMatrix.setCell(i, j,  B.personalMatrix.getCell(i, j));
						dealbreaker = 1;
					}
					
				}
				
			}
		}
		
		System.out.println("Parent A: " + A.personalMatrix.toString());
		System.out.println("Parent B: " + B.personalMatrix.toString());
		System.out.println("Child: " + child.personalMatrix.toString());
		
		return child;
	}

	public void Live(ArrayList<Citizen> curGen){
		//Spawn thread for each citizen.
		//in that thread, have the citizen run the needleman wunsch algo with their unique
		//similarity matrix. Based off of how good they do against the data, assign them
		//a fitness score.
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
				System.out.println("got one!");
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
}
