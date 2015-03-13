package client;

import interfaces.Server;

import java.util.List;
import cuneiform.Citizen;
import cuneiform.FoundDate;

public class MineCart implements Runnable{
	float fitness = -1;
	Citizen cit; 
	final List<FoundDate> attestations; 
	final Server server;
	private Exception ex; 
	
	private volatile boolean done = false;
	
	public MineCart(Citizen cit, final List<FoundDate> attestations, final Server server){
		this.cit = cit;
		this.attestations = attestations;
		this.server = server;
	}
		
	void setFitness() throws Exception {
		System.out.println("Citizen " + cit.IDNo + " sent to server.");
		this.fitness = server.live(cit, attestations);
		cit.setFitness(this.fitness);
		System.out.println("Citizen " + cit.IDNo + " came back with fitness " + this.fitness + " and has been assigned fitness of " + cit.getFitness());
	}
	
	public boolean isDone() {
		return this.done; 
	}
	
	public Exception getException() {
		return this.ex;
	}
	
	
	public float getFitness(){
		
		cit.setFitness(this.fitness);//XXX?
		return this.fitness;
	}
	
	@Override
	public void run() {
		try {
			setFitness();
		} catch (Exception e) {
			this.ex = e;
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		this.done = true; // bleeeegh
	}
	
	@Override
	public String toString() {
		return "Cit: " + cit.IDNo + " Fit: " +fitness;
		
	}

}
