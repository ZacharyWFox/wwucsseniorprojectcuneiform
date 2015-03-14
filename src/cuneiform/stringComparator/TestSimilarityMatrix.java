package cuneiform.stringComparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import cuneiform.Citizen;
import cuneiform.DateExtractor;

//this is so we can test expirement without dealing with completly implemented simMatrix
//also has a main to test the similarity matrix with

//NOTE 3/13/15: This is basically where I test the bits and pieces of diff classes now.
//not just simMatrix anymore




public class TestSimilarityMatrix extends SimilarityMatrix {

	private static String outputGenList = "data/CurGen";
	private static String statusFilePath = "data/status.txt";
	public static void main(String[] args) {
//		String[] input = {"asdfas", "{fds}argl", "jklsdf-{blah}", "{}df"   };
//		DateExtractor blah = new DateExtractor("ST");
//		String[] output = blah.separateDeterminants(input);
//		System.out.println(output);
		
		
		
//		//make sure actually can read from alphabet and randomize correctly
//		SimilarityMatrix test = new SimilarityMatrix();
//		try{
//			test.setCell(2, 6, (byte) 0);
//		}
//		catch (Exception e){
//			System.out.println(e.getMessage() + "sdfsd");
//			e.printStackTrace();
//		}
//		int x = test.colLength();
//		test.setCell(6,2, (byte) 0);
//		
//		SimilarityMatrix MutateTest = Mutate(test);
//		SimilarityMatrix MutateTwo = Mutate(MutateTest);
//		
//		try {
//			test.readMatrix("data/WriteTest.txt");
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.out.println("orig after reading from file: \n" + test.toString());
	
		
		
		
		
//		for (int i = 0; i < 10; i++){
//			try {
//				BufferedWriter out = new BufferedWriter(new FileWriter(statusFilePath, true));
//				out.append("Gen:" + (4+i) + 
//						" topFit:" + 5 +
//						" fitChange:" + 3 +
//						   " time:" + 123);
//				out.append("\n");
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
		
		
	}
	
	
	
	
	
	
	
	byte[][] simMatrix;
	
	
	public TestSimilarityMatrix() {
		super();	
		simMatrix = new byte[5][5];
	}
	
	public TestSimilarityMatrix(byte[][] matrix) {
		super();	
		simMatrix = matrix;
	}
	
	public TestSimilarityMatrix(SimilarityMatrix similarityMatrix) {
		
	}

	public void randomizeMatrix(){
		if (simMatrix == null){
			simMatrix = new byte[5][5];
		}
		
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 5; j++){
				simMatrix[i][j] = ((byte) Math.floor(Math.random() * 100));
			}
		}
	}
	
	public boolean setCell(int x, int y, byte val) {
		simMatrix[x][y] = val;
		
		return true;
	}
	
	public byte getCell(int x, int y){
		return simMatrix[x][y];
	}
	
	//return row length
		public int rowLength(){
			return 5;
		}
		
		//return col length
		public int colLength(){
			return 5;
		}

		
		public TestSimilarityMatrix clone(){
			return new TestSimilarityMatrix(simMatrix.clone());
		}
		
		public String toString(){
			String Alignment = "[ ";
        	for (int i = 0; i < rowLength(); i++) {
        			Alignment +="[ ";
        		
                for(int j = 0; j < colLength(); j++) {
                	Alignment += simMatrix[i][j] + " ";
                }
                Alignment += "]\n";
        	}
        	Alignment += "]";
        	
        	return Alignment;
		}
		
		public static SimilarityMatrix Mutate(SimilarityMatrix A){

			//Modify citizen's similarity matrix. 
			//limit number of cells to mutate to max 1%
			//limit numbers in the similarity matrix to <= 127 and >= -127, since it's stored in bytes (largest num represent is 127)
			Citizen mutant = new Citizen();
			mutant.personalMatrix = A.clone();
			
			
			
			int Xmax = A.rowLength() - 1;
			int Ymax = A.colLength() - 1;
			int loopMax = (int) (Math.floor(10 * .01));
			
			for (int i = 0; i < loopMax; i++){
				
				int x = (int) (Math.floor(Xmax * Math.random()));
				int y = (int) (Math.floor(Ymax * Math.random()));
				byte newVal = (byte) (Math.floor(127 * Math.random()));
				if (Math.random() > .5){
					newVal = (byte) -newVal;
				}
				
				try {
					mutant.personalMatrix.setCell(x, y, newVal);
				} catch (Exception e) {

					System.out.println("Something went horribly wrong (or there is an off by one) in mutate:" + e.getMessage());
					e.printStackTrace();
					i--;
				}
			}
			
			
			
			
			return mutant.personalMatrix;
		}
}
