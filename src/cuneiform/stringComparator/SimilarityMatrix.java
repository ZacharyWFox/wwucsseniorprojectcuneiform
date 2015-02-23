package cuneiform.stringComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimilarityMatrix implements Cloneable{

	// Lower triangular adjacency matrix
	private ArrayList<byte[]> dynamicMatrix;
	
	private Map<String, Integer> alphabet = new HashMap<String, Integer>();
	public SimilarityMatrix() {
		// TODO Auto-generated constructor stub
		try 
		{
			 //XXX
			/*
			readAlphabet(Parser.alphabetFilePath);
			readMatrix(Parser.alphabetFilePath); 
			*/
		}
		catch (Exception e)
		{
			System.out.printf("Something went wrong:/n/s/n", e.getMessage());
		}
		testAlphabetGen();
		allocateMatrix(alphabet.size());
		randomizeMatrix();
	}
	
	public SimilarityMatrix(SimilarityMatrix existing){
		this.alphabet = existing.alphabet;
		this.dynamicMatrix = existing.dynamicMatrix;
	}
	//XXX
	private void testAlphabetGen(){
		String[] english = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
		int i = 0;
		
		for (String s : english) {
			alphabet.put(s, i++);
		}
	}
	
	private void allocateMatrix(int size)
	{
		dynamicMatrix = new ArrayList<byte[]>(size);
		for ( int i = 1; i <= dynamicMatrix.size(); ++i){
			// Shrink number of columns with each row since we're diagonal
			dynamicMatrix.add(new byte[i]);
		}
	}
	
	private void readAlphabet(String filePath) throws Exception
	{
		throw new Exception("Not Implemented");
		
		//while ()
		// TODO: Implement
		// TODO: Read in alphabet
		// TODO: Set the number of letters
		
	}
	
	private void readMatrix(String filePath) throws Exception
	{
		throw new Exception("Not Implemented");
	}
	
	public byte score(String graphemeA, String graphemeB) throws Exception
	{
		// Get the index of the graphemes
		Integer indexA = this.alphabet.get(graphemeA);
		Integer indexB = this.alphabet.get(graphemeB);
		
		if (indexA == null || indexB == null){
			// If the either of the index lookups failed, we need to whine
			throw new Exception("Grapheme not in alphabet.");
		}
		
		return getCell(indexA, indexB);		
	}

	//TODO implement real version
	public void randomizeMatrix(){
		for (byte[] row :dynamicMatrix) {
			for (byte b : row) {
				b = (byte) Math.floor(Math.random() * 127);
			}
		}
	}
	
	public boolean setCell(int x, int y, byte val) {
		// Get the row so we can get the byte array and get the column 
		byte[] row = dynamicMatrix.get(x);
		// How much we need to shrink everything in order to ge
		//int difference = Math.abs(indexA - indexB);
		if (row.length < y){
			// Caller reversed the rows and the columns
			// So we reverse them
			row = dynamicMatrix.get(y);
			if (row.length < x){
				// Entry doesn't exist
				return false;
			} else {
				// TODO: make sure this works
				row[x] = val;
				dynamicMatrix.set(y, row);
			}
		} else {
			row[y] = val;
			dynamicMatrix.set(x, row);
		}
		return true;
	}

	public byte getCell(int x, int y) throws Exception {
		// Get the row so we can get the byte array and get the column 
		byte[] row = dynamicMatrix.get(x);
		// How much we need to shrink everything in order to ge
		//int difference = Math.abs(indexA - indexB);
		if (row.length < y){
			// Caller reversed the rows and the columns
			// So we reverse them
			row = dynamicMatrix.get(y);
			if (row.length < x){
				// Entry doesn't exist
				throw new Exception("Grapheme not found in similarity matrix.");
			} else {
				return row[x];
			}
		} else {
			return row[y];
		}
	}
	
	//return row length
	public int rowLength(){
		return alphabet.size();
	}
	
	//return col length
	public int colLength(){
		return alphabet.size();
	}
	
	
	//overrides TODO: test
	
	@Override
	public String toString(){
		StringBuilder retString = new StringBuilder();
		retString.append("\n{");
		for (byte[] row : dynamicMatrix){
			retString.append("{");
			for (byte b : row){
				retString.append(b + ", ");	
			}
			retString.delete(retString.length() - 2, retString.length());
			retString.append("},\n");
		}
		retString.deleteCharAt(retString.length() - 1);
		retString.append("}");
		return retString.toString();
	}
	
	
	//use this in mutate
	public SimilarityMatrix clone(){
		return new SimilarityMatrix(this);
	}
	
	
	
}
