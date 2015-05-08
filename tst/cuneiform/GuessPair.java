package cuneiform;

import cuneiform.stringComparator.Confidence;

/**
 * Holds a correct attestation and a guess made by a Date Extractor. 
 * Used for fitness calculations. 
 * @author ZacharyWFox
 */
public class GuessPair {
	KnownDate correct;
	FoundDate guess;
	public GuessPair(KnownDate correct, FoundDate guess) {
		this.correct = correct;
		this.guess = guess;
	}
	
	public KnownDate getCorrectDate() {
		return correct;
	}
	
	public FoundDate getGuess() {
		return guess;
	}
	public KnownDate getGuessDate() {
		return guess.getKnownDate();
	}
	
	public boolean isMatch() {
		return this.correct.getID() == this.guess.getKnownDate().getID();
	}
	
	public Confidence getConfidence() {
		return this.guess.confidence;
	}
}
