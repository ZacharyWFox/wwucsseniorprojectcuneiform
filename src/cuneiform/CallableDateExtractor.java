package cuneiform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import cuneiform.stringComparator.Confidence;
import cuneiform.stringComparator.SimilarityMatrix;

/**
 * A wrapper for the DateExtractor to make it a callable. This is used server side.
 * @author ZacharyWFox
 */
public class CallableDateExtractor implements Callable<List<GuessPair>>{
	
	DateExtractor extranctinator;
	SimilarityMatrix simMat;
	List<FoundDate> sample;
	boolean compare;
	
	// this extracts dates and only dates...parallelelely.
	public CallableDateExtractor(List<KnownDate> months, List<KnownDate> years, List<FoundDate> sample, SimilarityMatrix sim) {
		this.extranctinator = new DateExtractor(months, years);	
		this.simMat = sim;
		this.sample = sample;
		this.compare = false;
	}
	
	public CallableDateExtractor(List<KnownDate> months, List<KnownDate> years, List<FoundDate> sample, SimilarityMatrix sim, boolean compare) {
		this.extranctinator = new DateExtractor(months, years);	
		this.simMat = sim;
		this.sample = sample;
		this.compare = compare;
	}
	
	@Override
	public List<GuessPair> call() throws Exception {
		List<GuessPair> guesses = new ArrayList<GuessPair>(250);
		guesses = this.extranctinator.alignYears(this.sample, this.simMat, compare);
		return guesses;
	}
	
}
