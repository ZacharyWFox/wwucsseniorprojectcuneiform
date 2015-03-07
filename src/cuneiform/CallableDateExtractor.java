package cuneiform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import cuneiform.stringComparator.Confidence;
import cuneiform.stringComparator.SimilarityMatrix;

public class CallableDateExtractor implements Callable<List<GuessPair>>{
	
	DateExtractor extranctinator;
	SimilarityMatrix simMat;
	List<FoundDate> sample;
	// this extracts dates and only dates...parallelelely.
	public CallableDateExtractor(List<KnownDate> months, List<KnownDate> years, List<FoundDate> sample, SimilarityMatrix sim) {
		this.extranctinator = new DateExtractor(months, years);	
		this.simMat = sim;
		this.sample = sample;
	}
	
	@Override
	public List<GuessPair> call() throws Exception {
		// TODO Auto-generated method stub
		
		List<GuessPair> guesses = new ArrayList<GuessPair>(250);
		FoundDate attestation; //TODO: get from factory
		FoundDate curFound;
		GuessPair guess;
		
//		@SuppressWarnings("unchecked")
//		List<KnownDate> clone = (List<KnownDate>)((ArrayList<KnownDate>)this.extranctinator.knownYears).clone();
		guesses = this.extranctinator.alignYears(this.sample, this.simMat);
		
		//TODO: LOOP
//		while((attestation = DateFactory.get()) != null) {
//		
//		attestation = null; //TODO: get from factory
//		found = this.extranctinator.alignDateString(unknownGraphemes);
//		guess = new GuessPair(attestation.getKnownDate(), found);
//		
//		guesses.add(guess);
//		}
		
		return guesses;
	}
	
}
