package cuneiform.stringComparator;

//this is so we can test expirement without dealing with completly implemented simMatrix
//also has a main to test the similarity matrix with




public class TestSimilarityMatrix extends SimilarityMatrix {

	
	
	public static void main(String[] args) {
		
		//make sure actually can read from alphabet and randomize correctly
		SimilarityMatrix test = new SimilarityMatrix("data/test.txt");

		
		try {
			test.readMatrix("data/WriteTest.txt");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("orig after reading from file: \n" + test.toString());
	
		
		
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
		
		
}
