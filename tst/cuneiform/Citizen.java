package cuneiform;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cuneiform.stringComparator.SimilarityMatrix;




public class Citizen implements Comparable<Citizen>, Runnable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2504986002193445538L;
	public SimilarityMatrix personalMatrix;
	public Float fitness = -1F; // Default to invalid state
	public int IDNo;
	private Future<Float> futureFitness;
	private FutureState futureState = FutureState.UNBORN;
	
	//Constructors
	public Citizen() {
		personalMatrix = new SimilarityMatrix();
	}
	
	public Citizen(int id){
		
		personalMatrix = new SimilarityMatrix();
		IDNo = id;
		//XXX For testing purposes XXX
		fitness = (float) Math.floor(Math.random() * 100);
	}
	
	public Citizen(int id, SimilarityMatrix matrix){
		
		personalMatrix = matrix;
		IDNo = id;
		//XXX For testing purposes XXX
		fitness = (float) Math.floor(Math.random() * 100);
	}
	
	/***
	 * Blocks until the Future fitness is ready, then sets it in this Citizen
	 * @return Returns true if fitness was successfully set or was in the past.
	 */
	public boolean evaluateFitness() {
		switch (getFutureState()){
		case ALIVE :
			try {
				this.fitness = this.futureFitness.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			}
			break;
		case DEAD:
			if(fitness < 0) {
				return false;
			}
			break;
		case UNBORN:
			return false;
		case CANCELLED:
			return false;
		default:
			return false;
		}
		return true;
	}
	
	public boolean isFitnessReady() {
		if (this.getFutureState() == FutureState.DEAD) {
			return true;
		} else {
			return false;
		}
	}
	
	public FutureState getFutureState() {
		if (this.futureFitness == null) {
			this.futureState = FutureState.UNBORN;
		} else if (this.futureFitness.isCancelled()){
			this.futureState = FutureState.CANCELLED;
		} else if (this.futureFitness.isDone()) {
			this.futureState = FutureState.DEAD;
		} else {
			this.futureState = FutureState.ALIVE;
		}
		return this.futureState;
	}

	@Override
	public int compareTo(Citizen citizen) {
		return Float.compare(citizen.fitness, this.fitness);
		
	}
	
	@Override
	public String toString(){
		return "[Fitness: " +  Float.toString(fitness) + " Citizen ID: " + Integer.toString(IDNo) + "]";
		
	}
	
	@Override
	//TODO: XXX
	public void run() {
		//then figure out fitness
		
		System.out.println("Citizen No: " + IDNo + " is running.");
		//Submit live
		
		
		
		//EvaluateFitness();
	}
	
	 /***
	  * Get the fitness. PLEASE USE isFitnessReady first, as The value may be invalid (negative) for calculations.
	  * @return The fitness of this citizen. If the fitness has not been evaluated, then the result will be negative.
	  */
	public float getFitness() {
		return this.fitness;
	}
	
	public float getFitnessTest() {
		//XXX For testing purposes
		if (this.fitness > 0)
			return this.fitness;
		else
			return this.fitness = (float) Math.floor(Math.random() * 100);
	}
	
	
	
}