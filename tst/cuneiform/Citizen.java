package cuneiform;

import java.util.List;

import cuneiform.stringComparator.SimilarityMatrix;

public class Citizen implements Comparable<Citizen>, Runnable{

	public SimilarityMatrix personalMatrix;
	public int fitness;
	public int IDNo;
	private int threads = 1; //TODO: add the number of threads we want (method or here)
	
	//Constructors
	public Citizen() {
		personalMatrix = new SimilarityMatrix();
	}
	
	public Citizen(int id){
		
		personalMatrix = new SimilarityMatrix();
		IDNo = id;
		//XXX For testing purposes XXX
		fitness = (int) Math.floor(Math.random() * 100);
	}
	
	public Citizen(int id, SimilarityMatrix matrix){
		
		personalMatrix = matrix;
		IDNo = id;
		//XXX For testing purposes XXX
		fitness = (int) Math.floor(Math.random() * 100);
	}

	
	
	//Overrides

	@Override
	public int compareTo(Citizen citizen) {
		return Integer.compare(citizen.fitness, this.fitness);
		
	}
	
	@Override
	public String toString(){
		return "[Fitness: " +  Integer.toString(fitness) + " Citizen ID: " + Integer.toString(IDNo) + "]";
		
	}


	@Override
	public void run() {
		// TODO run the needleman-wunsch algo with personalMatrix
		//then figure out fitness
		ParallelDateExtractor extractor = new ParallelDateExtractor();
		
		
		extractor.call(); //TODO: capture return value
		
		cuneiform.stringComparator.SumerianNWSubstringComparator.setSimilarityMatrix(this);
		
		System.out.println("Citizen No: " + IDNo + " is running.");
		// TODO: add new DateExtractor and extract some dates
		//EvaluateFitness();
	}
	
	public int getFitness() {
		return this.fitness;
	}
	
	private void EvaluateFitness(List<GuessPair> guesses) {
		//TODO: implement
		int correct = 0;
		for(GuessPair g : guesses) {
			if(g.isMatch()) {
				correct++;
			}
		}
		
		if (guesses.isEmpty()) {
			System.out.println ("ERROR: Recieved empty list of guesses. That's bad.");
			return;
			// TODO: throw exception?
		}
		
		fitness = Math.abs(correct/guesses.size());
	}
}


}
