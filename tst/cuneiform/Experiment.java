package cuneiform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Experiment {

	
	ArrayList<Citizen> Population;
	int GenerationNo;
	Citizen bestCit;
	
	
	public static void main(String[] args){
		
		Experiment blah = new Experiment();
		Citizen arg = new Citizen();
		System.out.println(Integer.toString(arg.personalMatrix.matrix[0].length));
		blah.runExperiment();
	}
	
	
	public Experiment() {
		// intialize stuff.
		Population = new ArrayList<Citizen>();
		GenerationNo = 1;
	}
	
	 public void runExperiment(){
		
		
		BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));
		
		//make random starting pop.
		for (int x = 0; x < 10; x++){
			Citizen newCitizen = new Citizen();
			
			for(int i = 0; i < newCitizen.personalMatrix.matrix.length; i++){
				for (int j = 0; j < newCitizen.personalMatrix.matrix[0].length; j++){
			
					
					
				}
			}
			
			
		}
		
		
		while (true){
			
			Live(Population);
			Collections.sort(Population);
			//TODO: Top 10 are immortal
			//rest of population for new gen is created from
			//crossovers of current gen (randomized over entire pop, lean towards top 10 for crossover, lean away from bottom) 
			//with small num having mutations			
			
			
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
		}
		
		System.out.println("The program has ended. Here are the results: ");
		
		System.out.println("Generation No: " + GenerationNo);
		//System.out.println("Best citizen: " + bestCit.toString());

		
			
	}
	
	
	public Citizen Mutate(Citizen A){
		//Modify citizen's similarity matrix. 
		//limit number of cells to mutate to max 100
		//limit numbers in the similarity matrix to <= 150
		int max = A.personalMatrix.matrix.length;
		int loopMax = (int) (Math.floor(100 * Math.random()));
		
		for (int i = 0; i < loopMax; i++){
			
			int x = (int) (Math.floor(max * Math.random()));
			int y = (int) (Math.floor(max * Math.random()));
			int add = (int) (Math.floor(50 * Math.random()));
			
			A.personalMatrix.matrix[x][y] = (A.personalMatrix.matrix[x][y] + add) % 150;
			
		}
		
		
		
		return A;
	}
	
	public Citizen Crossover(Citizen A, Citizen B){
		//Create new citizen, for each place in matrix,
		//coin flip to take from parent A or B
		Citizen child = new Citizen();
		int dealbreaker = 0;
		
		
		for(int i = 0; i < child.personalMatrix.matrix.length; i++){
			for (int j = 0; j < child.personalMatrix.matrix[0].length; j++){
				double coin = Math.random();
				
				if (coin > .5){
					child.personalMatrix.matrix[i][j] = A.personalMatrix.matrix[i][j];
				}
				else if (coin < .5){
					child.personalMatrix.matrix[i][j] = B.personalMatrix.matrix[i][j];
				}
				else{
					if (dealbreaker == 1){
						child.personalMatrix.matrix[i][j] = A.personalMatrix.matrix[i][j];
						dealbreaker = 0;
					}
					else{
						child.personalMatrix.matrix[i][j] = B.personalMatrix.matrix[i][j];
						dealbreaker = 1;
					}
					
				}
				
			}
		}
		
		
		return child;
	}

	public void Live(ArrayList<Citizen> curGen){
		//TODO: Spawn thread for each citizen.
		//in that thread, have the citizen run the needleman wunsch algo with their unique
		//similarity matrix. Based off of how good they do against the data, assign them
		//a fitness score.
	}
	
	
}
