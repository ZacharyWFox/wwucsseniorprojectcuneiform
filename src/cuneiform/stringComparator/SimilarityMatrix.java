package cuneiform.stringComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cuneiform.Parser;

public class SimilarityMatrix implements Cloneable{

	private ArrayList<byte[]> dynamicMatrix;
	private int numLetters = 10; //TODO: Remove when implemented
	
	private Map<String, Integer> alphabet = new HashMap<String, Integer>();
	public SimilarityMatrix() {
		// TODO Auto-generated constructor stub
		try 
		{
			/*
			ReadAlphabet(Parser.alphabetFilePath);
			ReadMatrix(Parser.alphabetFilePath); 
			*/
		}
		catch (Exception e)
		{
			System.out.printf("Something went wrong:/n/s/n", e.getMessage());
		}
		AllocateMatrix();
	}
	
	private void AllocateMatrix()
	{
		dynamicMatrix = new ArrayList<byte[]>(alphabet.size());
		for ( int i = 1; i <= dynamicMatrix.size(); ++i){
			// Shrink number of columns with each row since we're diagonal
			dynamicMatrix.add(new byte[i]);
		}
	}
	
	private void ReadAlphabet(String filePath) throws Exception
	{
		throw new Exception("Not Implemented");
		
		//while ()
		// TODO: Implement
		// TODO: Read in alphabet
		// TODO: Set the number of letters
		
	}
	
	private void ReadMatrix(String filePath) throws Exception
	{
		throw new Exception("Not Implemented");
	}
	
	public byte Score(String graphemeA, String graphemeB) throws Exception
	{
		// Get the index of the graphemes
		Integer indexA = this.alphabet.get(graphemeA);
		Integer indexB = this.alphabet.get(graphemeB);
		
		if (indexA == null || indexB == null){
			// If the either of the index lookups failed, we need to whine
			throw new Exception("Grapheme not in alphabet.");
		}
		
		// Get the row so we can get the byte array and get the column 
		byte[] row = dynamicMatrix.get(indexA);
		// How much we need to shrink everything in order to ge
		//int difference = Math.abs(indexA - indexB);
		if (row.length < indexB){
			// Caller reversed the rows and the columns
			// So we reverse them
			row = dynamicMatrix.get(indexB);
			if (row.length < indexA){
				// Entry doesn't exist
				throw new Exception("Grapheme not found in similarity matrix.");
			} else {
				return row[indexA];
			}
		} else {
			return row[indexB];
		}
	}
	
	
	//TODO implmement real version
	public void randomizeMatrix(){
		for (int i = 0; i < 5; i++){
			for (int j=0; j < 5; j++){
				testMatrix[i][j] = (int) Math.floor(Math.random() * 127);
			}
		}
	}
	
	
	
	//Getters and setters TODO: implement below getters and setters
	
	private int[][] testMatrix = new int[5][5]; //for testing purposes XXX
	//get a cell in the matrix based off row and col (x and y)
	//return -1 if can't find cell
	public int getCell(int x, int y){
		return testMatrix[x][y];
	}
	
	//set cell in matrix based off row and col
	//return true if succeed in setting
	public boolean setCell(int x, int y, int val){
		testMatrix[x][y] = val;
		
		return true;
	}
	
	//return row length
	public int rowLength(){
		return testMatrix.length;
	}
	
	//return col length
	public int colLength(){
		return testMatrix[0].length;
	}
	
	
	//overrides TODO: implement
	
	@Override
	public String toString(){
		String retString = "\n";
		for (int i = 0; i < 5; i++){
			retString += "[";
			for (int j = 0; j < 5; j++){
				retString += testMatrix[i][j] + ", ";	
			}
			retString += "]\n";
		}
		
		return retString;
	}
	
	
	//use this in mutate
	public SimilarityMatrix clone(){
		
		return (SimilarityMatrix) this.clone();
		
	}
	
	
	
}
