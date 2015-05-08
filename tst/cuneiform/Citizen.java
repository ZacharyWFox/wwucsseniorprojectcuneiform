package cuneiform;

import interfaces.Server;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import cuneiform.FutureState;
import cuneiform.stringComparator.SimilarityMatrix;

/**
 * This is where the Similarity Matrix (The thing we're mutating) lives. 
 * Also tracks fitness of given similarity matrix
 * @author ZacharyWFox
 * @author DThurow
 */
public class Citizen implements Comparable<Citizen>, Serializable{

	/**
	 * For serialization purposes
	 */
	private static final long serialVersionUID = 2504986002193445538L;
	public SimilarityMatrix personalMatrix;
	private Float fitness = -1F; // Default to invalid state
	public int IDNo;
	private volatile FutureState futureState = FutureState.UNBORN;

	//Constructors
	public Citizen() {
		personalMatrix = new SimilarityMatrix();
	}
	
	public Citizen(int id){
		
		personalMatrix = new SimilarityMatrix();
		IDNo = id;
	}
	
	public Citizen(int id, SimilarityMatrix matrix){
		
		personalMatrix = matrix;
		IDNo = id;
	}
	
	/***
	 * Blocks until the Future fitness is ready, then sets it in this Citizen
	 * @return Returns true if fitness was successfully set or was in the past.
	 */
	
	public void setFitness(float fit) {
		this.fitness = fit;
		this.futureState = FutureState.DEAD;
	}
	
	public void resetState() {
		this.futureState = FutureState.UNBORN;
	}
	
	public void resetFitness() {
		this.fitness = -1F;
	}
	
	public void rebirth() {
		resetState();
		this.IDNo = 0;
		resetFitness();
		this.personalMatrix = null;
	}

	public boolean isFitnessReady() {
		if (this.getFutureState() == FutureState.DEAD) {
			return true;
		} else {
			return false;
		}
	}
	
	public FutureState getFutureState() {

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
	
	 /***
	  * Get the fitness. PLEASE USE isFitnessReady first, as The value may be invalid (negative) for calculations.
	  * @return The fitness of this citizen. If the fitness has not been evaluated, then the result will be negative.
	  */
	public float getFitness() {
		return this.fitness;
	}
}
