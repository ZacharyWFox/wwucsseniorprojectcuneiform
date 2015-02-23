package cuneiform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cuneiform.stringComparator.SimilarityMatrix;

public class Citizen implements Comparable<Citizen>, Runnable{

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
		
		cuneiform.stringComparator.SumerianNWSubstringComparator.setSimilarityMatrix(this);
		
		System.out.println("Citizen No: " + IDNo + " is running.");
		
	}
	
	public int getFitness() {
		return this.fitness;	
	}
	
	private void EvaluateFitness() {
		//TODO: implement
	}


}
