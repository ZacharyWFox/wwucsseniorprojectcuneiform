package cuneiform;

import java.io.Serializable;
import java.util.List;

import cuneiform.stringComparator.SimilarityMatrix;


public class Citizen implements Comparable<Citizen>, Runnable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 558045442181825459L;
	public SimilarityMatrix personalMatrix;
	public int fitness;
	public int IDNo;
	
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
		
		System.out.println("Citizen No: " + IDNo + " is running.");
		//Submit live
		
		try {
			//extractor.call(); //TODO: implement
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //TODO: capture return value
		
		
		
		// TODO: add new DateExtractor and extract some dates
		//EvaluateFitness();
	}
	
	public int getFitness() {
		//XXX For testing purposes
		if (this.fitness > 0)
			return this.fitness;
		else
			return this.fitness = (int) Math.floor(Math.random() * 100);
	}
	
	
}
