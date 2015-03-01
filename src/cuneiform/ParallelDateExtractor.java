package cuneiform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import cuneiform.stringComparator.Confidence;

public class ParallelDateExtractor implements Callable<List<GuessPair>>{
	
	DateExtractor extranctinator;
	// this extracts dates and only dates...parallelelely.
	public ParallelDateExtractor(List<KnownDate> months, List<KnownDate> years) {
		this.extranctinator = new DateExtractor(months, years);
	}
	
	@Override
	public List<GuessPair> call() throws Exception {
		// TODO Auto-generated method stub
		
		List<GuessPair> guesses = new ArrayList<GuessPair>(300);
		FoundDate attestation; //TODO: get from factory
		FoundDate found;
		GuessPair guess;
		
		this.extranctinator.alignYearsTest();
		//TODO: LOOP
//		while((attestation = DateFactory.get()) != null) {
//		
//		attestation = null; //TODO: get from factory
//		found = this.extranctinator.alignDateString(unknownGraphemes);
//		guess = new GuessPair(attestation.getKnownDate(), found);
//		
//		guesses.add(guess);
//		}
		
		return null;
	}
	
}
