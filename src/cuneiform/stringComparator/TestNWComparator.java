package cuneiform.stringComparator;

public class TestNWComparator {

	public TestNWComparator() {
		
	}

	public static void main(String[] args) {
		String known = "a d c";
		String[] unknowngraphemes = { "a",  "d", "c", "b", "k" };
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