package cuneiform;

public class Citizen implements Comparable<Citizen>{

	public SimilarityMatrix personalMatrix;
	public int Fitness;
	public int IDNo;
	
	
	public Citizen() {
		// TODO Auto-generated constructor stub
		personalMatrix = new SimilarityMatrix();
	}


	@Override
	public int compareTo(Citizen citizen) {
		return Integer.compare(this.Fitness, citizen.Fitness);
		
	}
	
	@Override
	public String toString(){
		return "[Fitness: " +  Integer.toString(Fitness) + " Citizen ID: " + Integer.toString(IDNo) + "]";
		
	}

}
