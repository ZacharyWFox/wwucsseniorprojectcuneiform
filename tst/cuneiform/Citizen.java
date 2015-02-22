package cuneiform;

import cuneiform.stringComparator.SimilarityMatrix;

public class Citizen implements Comparable<Citizen>, Runnable{

	public SimilarityMatrix personalMatrix;
	public int Fitness;
	public int IDNo;
	
	
	
	//Constructors
	public Citizen() {
		personalMatrix = new SimilarityMatrix();
			
	}
	
	public Citizen(int id){
		
		personalMatrix = new SimilarityMatrix();
		IDNo = id;
		//XXX For testing purposes XXX
		Fitness = (int) Math.floor(Math.random() * 100);
	}
	
	public Citizen(int id, SimilarityMatrix matrix){
		
		personalMatrix = matrix;
		IDNo = id;
		//XXX For testing purposes XXX
		Fitness = (int) Math.floor(Math.random() * 100);
	}

	
	
	//Overrides

	@Override
	public int compareTo(Citizen citizen) {
		return Integer.compare(citizen.Fitness, this.Fitness);
		
	}
	
	@Override
	public String toString(){
		return "[Fitness: " +  Integer.toString(Fitness) + " Citizen ID: " + Integer.toString(IDNo) + "]";
		
	}


	@Override
	public void run() {
		// TODO run the needleman-wunsch algo with personalMatrix
		//then figure out fitness
		System.out.println("Citizen No: " + IDNo + " is running.");
		
	}


}
