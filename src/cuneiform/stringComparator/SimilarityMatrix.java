package cuneiform.stringComparator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cuneiform.Parser;

public class SimilarityMatrix implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5501164272923209758L;
	private static final String alphabetFilePath = "data/signs.txt"; //relative to wwucsseniorprojectcuneiform
	// Lower triangular adjacency matrix
	private ArrayList<byte[]> dynamicMatrix;
	private byte[] minVal;
	
	private Map<String, Integer> alphabet = new HashMap<String, Integer>();
	public SimilarityMatrix() {
		try 
		{
			if (alphabet.size() == 0){
				readAlphabet(alphabetFilePath);
			}
			//readMatrix(Parser.alphabetFilePath); 
			
		}
		catch (Exception e)
		{
			System.out.printf("Something went wrong:/n/s/n", e.getMessage());
		}
		//testAlphabetGen();
		minVal = new byte[alphabet.size()];
		allocateMatrix(alphabet.size());
		randomizeMatrix();
	}
	
	public SimilarityMatrix(String filename){
		try {
			readAlphabet(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		allocateMatrix(alphabet.size());
		minVal = new byte[alphabet.size()];
		randomizeMatrix();
	}
	
	public SimilarityMatrix(SimilarityMatrix existing){
		//only difference will be the dynamicMatrix
		this.dynamicMatrix = new ArrayList<byte[]>();
		minVal = existing.minVal.clone();
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
		//starts relative file path from wwucsseniorprojectcuneiform
		
				BufferedReader in = new BufferedReader(new FileReader(filePath));
				String blah;
				int i = 0;
				while (in.ready()){
					blah = in.readLine().trim();
					alphabet.put(blah, i++);
				}
				in.close();
		
	}
	
	public void readMatrix(String filePath) throws Exception
	{
		//starts relative file path from wwucsseniorprojectcuneiform
		
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		dynamicMatrix = new ArrayList<byte[]>();
		minVal = new byte[alphabet.size()];
		String rowStr;
		
		while (in.ready()){
			rowStr = in.readLine();
			String[] cells = rowStr.split(",");
			
			byte[] row = new byte[cells.length];
			
			for (int i = 0; i < cells.length; i++){
				byte val = Byte.parseByte(cells[i].trim()); 
				row[i] =  val;
				if (minVal[i] > val){
					minVal[i] = val;
				}
				if (minVal[dynamicMatrix.size()] > val){
					minVal[dynamicMatrix.size()] = val;
				}
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
			
			return 0;
		}
		
		return getCell(indexA, indexB);		
	}

	
	public void randomizeMatrix(){
		for (int i = 0; i < dynamicMatrix.size(); i++) {
			byte[] curRow = dynamicMatrix.get(i);
			
			for (int x = 0; x < curRow.length; x++) {
				byte val = (byte) Math.floor(Math.random() * 127);
				  
				if (Math.random() > .5){
					val = (byte) -val;
				}
				curRow[x] = val;
				
				if (minVal[i] > val){
					minVal[i] = val;
				}
				if (minVal[x] > val){
					minVal[x] = val;
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
		
		if (x < alphabet.size() && minVal[x] > val){
			minVal[x] = val;
		}
		if (y < alphabet.size() && minVal[y] > val){
			minVal[y] = val;
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
	
	
	
	
	public String detectMetaData(String x) {
		String lower = x.toLowerCase();
		if (lower.contains("{ki}")) {
			return "{ki}";
		}
		
		return "";
	}
	
	//use this in mutate
	public SimilarityMatrix clone(){
		return new SimilarityMatrix(this);
	}
	
	public byte[] getRow(int x){
		return dynamicMatrix.get(x);
	}
	
	public byte getMin(String x){
		if(x == null || x.isEmpty()) {
			return 0;
		}
		
		int index = 0;
		if (alphabet.containsKey(x.trim())) {
			index = alphabet.get(x);
		}
		else {
			//System.out.println("Grapheme " + x + " was not in the alphabet, defaulting to 0.");
			return 0;
		}
		
		if (index >= 0 && index < minVal.length){
			return minVal[index];
		} else {
			return 0;
		}

	}
	
	
}
