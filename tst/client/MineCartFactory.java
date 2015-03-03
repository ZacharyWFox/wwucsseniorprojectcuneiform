package client;

import genetics.GeneticServer;

import java.util.List;
import java.util.concurrent.Callable;
import cuneiform.Citizen;
import cuneiform.FoundDate;

public class MineCartFactory {
	
	public static Callable<Float> build(final Citizen cit, final List<FoundDate> attestations, final GeneticServer server) {
		return new Callable<Float>() {

			@Override
			public Float call() throws Exception {
				return server.live(cit, attestations);
			}
			
		};
	}

}
