package cuneiform;

import java.util.List;
import java.util.concurrent.Callable;

public class ParallelDateExtractor implements Callable<List<GuessPair>>{
	
	// this extracts dates and only dates...parallelelely.
	public ParallelDateExtractor() {
		
	}

	//TODO: spawn threads
	
	@Override
	public List<GuessPair> call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
