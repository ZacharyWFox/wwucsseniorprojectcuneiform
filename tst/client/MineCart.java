package client;

import interfaces.Server;

import java.util.List;
import java.util.concurrent.Callable;
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
		this.fitness = server.live(cit, attestations);
		cit.fitness = this.fitness;
	}
	
	public boolean isDone() {
		return this.done; 
	}
	
	public Exception getException() {
		return this.ex;
	}
	
	
	public float getFitness(){
		
		cit.fitness = this.fitness;
		return this.fitness;
	}
	
	@Override
	public void run() {
		try {
			setFitness();
		} catch (Exception e) {
			this.ex = e;
		}
		this.done = true; // bleeeegh
	}

}
