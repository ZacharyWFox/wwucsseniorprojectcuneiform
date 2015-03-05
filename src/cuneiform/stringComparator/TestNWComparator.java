package cuneiform.stringComparator;

//tests the NWSubstrinComparator
//also used to test the similarity matrix class

public class TestNWComparator {

	public TestNWComparator() {
		
	}

	public static void main(String[] args) {
		
		SimilarityMatrix matrix = new SimilarityMatrix("data/test.txt");
		try {
			matrix.readMatrix("data/writeTest.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SumerianNWSubstringComparator.simMat = matrix;
		SumerianNWSubstringComparator.hasMatrix = true;
		
		String known = "one two three four five six seven";
		String[] unknowngraphemes = {  "seven" ,   "three",  "four", "two",  "six",  "five"};
		double[] conf = new double[2];
		int[] indx = new int[2];
		int[] dist = new int[2];
		
		System.out.println("levenshtien:\n");
		SumerianLevenstheinSubstringComparator.compare(known, unknowngraphemes, 0, conf, indx, dist);
		System.out.println("conf: " + conf[0]);
		System.out.println("indx: " + indx[0]);
		System.out.println("dist: " + dist[0]);
		
		
		System.out.println("NW:\n");
		SumerianNWSubstringComparator.compare(known, unknowngraphemes, 0, conf, indx, dist);
	
		
		System.out.println("conf: " + conf[0]);
		System.out.println("indx: " + indx[0]);
		System.out.println("dist: " + dist[0]);

	}

}