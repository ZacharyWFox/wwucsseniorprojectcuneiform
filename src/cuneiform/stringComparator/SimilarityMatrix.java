package cuneiform.stringComparator;

<<<<<<< HEAD
=======
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
>>>>>>> 5cdfa6ea887f81a301932a39fcf3b41e41a27f60
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cuneiform.Parser;

public class SimilarityMatrix implements Cloneable{

	// Lower triangular adjacency matrix
	private ArrayList<byte[]> dynamicMatrix;
	
	private Map<String, Integer> alphabet = new HashMap<String, Integer>();
	public SimilarityMatrix() {
		try 
		{
			if (alphabet.size() == 0){
				readAlphabet(Parser.alphabetFilePath);
			}
			
			//readMatrix(Parser.alphabetFilePath); 
			
		}
		catch (Exception e)
		{
			System.out.printf("Something went wrong:/n/s/n", e.getMessage());
		}
		//testAlphabetGen();
		allocateMatrix(alphabet.size());
		randomizeMatrix();
	}
	
<<<<<<< HEAD
=======
	public SimilarityMatrix(String filename){
		try {
			readAlphabet(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		allocateMatrix(alphabet.size());
		randomizeMatrix();
	}
	
>>>>>>> 5cdfa6ea887f81a301932a39fcf3b41e41a27f60
	public SimilarityMatrix(SimilarityMatrix existing){
		//only difference will be the dynamicMatrix
		this.dynamicMatrix = new ArrayList<byte[]>();
		
		for (byte[] row : existing.dynamicMatrix){
			this.dynamicMatrix.add(row.clone());
		}

	}
	
	//XXX for testing purposes only
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
		for ( int i = 1; i <= size; ++i){
			// Shrink number of columns with each row since we're diagonal
			dynamicMatrix.add(new byte[i]);
		}
	}
	
	private void readAlphabet(String filePath) throws Exception
	{
<<<<<<< HEAD
		throw new Exception("Not Implemented");
		
		//while ()
		// TODO: Implement
		// TODO: Read in alphabet
		// TODO: Set the number of letters
=======
		//starts relative file path from wwucsseniorprojectcuneiform
>>>>>>> 5cdfa6ea887f81a301932a39fcf3b41e41a27f60
		
	}
	
	public void readMatrix(String filePath) throws Exception
	{
		//starts relative file path from wwucsseniorprojectcuneiform
		
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		dynamicMatrix = new ArrayList<byte[]>();
		String rowStr;
		
		while (in.ready()){
			rowStr = in.readLine();
			String[] cells = rowStr.split(",");
			
			byte[] row = new byte[cells.length];
			
			for (int i = 0; i < cells.length; i++){
				row[i] =  Byte.parseByte(cells[i].trim());
			}
			
			dynamicMatrix.add(row);
		}
		in.close();
	}
	
	public void writeMatrix(String filePath) throws Exception{
		
		//starts relative file path from wwucsseniorprojectcuneiform
		
		BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
		String matrix = this.toString();
		matrix = matrix.replace('{', ' ');
		matrix = matrix.replace('}', ' ');
		out.write(matrix);
		out.close();
		
	}
	
	public byte score(String graphemeA, String graphemeB) throws Exception
	{
		// Get the index of the graphemes
		Integer indexA = this.alphabet.get(graphemeA);
		Integer indexB = this.alphabet.get(graphemeB);
		
		if (indexA == null || indexB == null){
			// If the either of the index lookups failed, we need to whine
			throw new Exception("grapheme not found!");
		}
		
		return getCell(indexA, indexB);		
	}

	
	public void randomizeMatrix(){
		for (int i = 0; i < dynamicMatrix.size(); i++) {
			byte[] curRow = dynamicMatrix.get(i);
			
			for (int x = 0; x < curRow.length; x++) {
				 curRow[x] = (byte) Math.floor(Math.random() * 127);
				if (Math.random() > .5){
					curRow[x] = (byte) -curRow[x];
				}
				
				
			}
		}
	}
	
	public boolean setCell(int x, int y, byte val) {
		// Get the row so we can get the byte array and get the column 
		byte[] row = dynamicMatrix.get(x);
		// How much we need to shrink everything in order to ge
		//int difference = Math.abs(indexA - indexB);
		if ((row.length-1) < y){
			// Caller reversed the rows and the columns
			// So we reverse them
			row = dynamicMatrix.get(y);
			if ((row.length-1) < x){
				// Entry doesn't exist
				return false;
			} else {
				
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
		if ((row.length-1) < y){
			// Caller reversed the rows and the columns
			// So we reverse them
			row = dynamicMatrix.get(y);
			if ((row.length-1) < x){
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
	
	public void setMatrix(ArrayList<byte[]> value) {
		this.dynamicMatrix = value;
	}
	//overrides
	
	@Override
	public String toString(){
		
		//NOTE: if this changes, it will affect the readMatrix and writeMatrix funcs above
		//tweak this function carefully
		StringBuilder retString = new StringBuilder();
		retString.append("{");
		for (byte[] row : dynamicMatrix){
			retString.append("{");
			for (byte b : row){
				retString.append(b + ", ");	
			}
			retString.delete(retString.length() - 2, retString.length());
			retString.append("}\n");
		}
		retString.deleteCharAt(retString.length() - 1);
		retString.append("}");
		return retString.toString();
	}
	
	
	//use this in mutate
	public SimilarityMatrix clone(){
<<<<<<< HEAD
		return new SimilarityMatrix(this);
=======
		return new SimilarityMatrix(this); 
>>>>>>> 5cdfa6ea887f81a301932a39fcf3b41e41a27f60
	}
	
	
	
}
