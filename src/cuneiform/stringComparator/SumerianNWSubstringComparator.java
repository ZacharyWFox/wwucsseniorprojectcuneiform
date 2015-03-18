package cuneiform.stringComparator;
import java.util.Arrays;
import cuneiform.stringComparator.SimilarityMatrix;


public class SumerianNWSubstringComparator {

    
	static SimilarityMatrix simMat;
	// A flag that prevents compares from accessing similarity matrix if it is null.
	static boolean hasMatrix = false;
    static boolean debug = false; // If true, print debug messages in this class
    // foundStart is an offset of the tablet text. fix this if we can.
    // 
    public static void compare(String known, String[] allFoundGraphemes, 
        final int foundStart, double[] conf, int[] indx, int[] dist) {
    		if (!hasMatrix){
    			System.out.print("No similarity matrix, defaulting to edit distance.");
    		}
            // Split string into graphemes
            String[] knownGraphemes = known.split("-| ");
            String[] foundGraphemes = Arrays.copyOfRange(allFoundGraphemes, foundStart, allFoundGraphemes.length);
            int xAlignLen = knownGraphemes.length + 1;
            int yAlignLen = foundGraphemes.length + 1;
            int[][] alignment = new int[xAlignLen][yAlignLen];
            
            // Initialize edges for alignment
            for (int i = 0; i < xAlignLen; i++) {
                alignment[i][0] = 1 * -i;
            }
            for (int j = 0; j < yAlignLen; j++) {
                alignment[0][j] = 1 * -j;
            }

            // Compute alignment matrix
            for (int i = 1; i < xAlignLen; i++) {
                for(int j = 1; j < yAlignLen; j++) {
                    int match  = alignment[i - 1][j - 1] + similarity(knownGraphemes[i-1], foundGraphemes[j-1]);
                    int delete = alignment[i - 1][j] + linGap();
                    int insert = alignment[i][j - 1] + linGap();
                    alignment[i][j] = max(match, delete, insert);
                    
                    
                }
            }
            int finalMatch = alignment[xAlignLen -1][yAlignLen -1];
            
            

            int bestValue = 0;
            for (int i = 0; i < knownGraphemes.length; i++){
            	bestValue += similarity(knownGraphemes[i], knownGraphemes[i]);
            }

            int worstVal = 0;
            for (int i = 0; i < knownGraphemes.length; i++){
            	worstVal += simMat.getMin(knownGraphemes[i]);
            }
            for (int i = 0; i < foundGraphemes.length - knownGraphemes.length; i++){
            	worstVal += linGap();
            }
        	
        	//if finalMatch is the same as bestValue, we get 1. as finalMatch gets closer
            //to the worst possible value, the top gets closer and closer to 0, making the confidence
            //go to 0
            
            
        	conf[0] = (100.0 * Math.abs(worstVal - finalMatch) / Math.abs(worstVal - bestValue)) ;
        	dist[0] = Math.abs(worstVal - finalMatch);
            indx[0] = 0;
            
            if (debug) {
            	String Alignment = "[ [ _ ";
            	for (int i = 0; i < foundGraphemes.length; i++){
            		Alignment += foundGraphemes[i] + " ";
            	}
            	Alignment += "]\n";
            	for (int i = 0; i < xAlignLen; i++) {
            		if (i == 0){
            			Alignment +="_ [ ";
            		}
            		else{
            			Alignment += knownGraphemes[i-1] + " [ ";
            		}
            		
                    for(int j = 0; j < yAlignLen; j++) {
                    	Alignment += alignment[i][j] + " ";
                    }
                    Alignment += "]\n";
            	}
            	Alignment += "]";
            	System.out.println("Alignment matrix:\n" + Alignment);
            	
            	
	            String[] optAligns = constructAlignment(alignment, knownGraphemes, foundGraphemes);
	            
	            System.out.printf("aligned\n%s and %s as\n%s\n%s\n%s\nindels: %s\n", 
	            		joinAlignment(knownGraphemes), joinAlignment(foundGraphemes), 
	            		optAligns[0], optAligns[1], optAligns[2], optAligns[3]);
            }
    }
    
    public static void compare(String known, String[] allFoundGraphemes, 
            final int foundStart, double[] conf, int[] indx, int[] dist, SimilarityMatrix sim) {

                // Split string into graphemes
                String[] knownGraphemes = known.split("- |-| ");
                if (debug){
                	System.out.println("known graphemes: " + Arrays.toString(knownGraphemes));
                }
                String[] foundGraphemes = Arrays.copyOfRange(allFoundGraphemes, foundStart, allFoundGraphemes.length);
                if (debug){
                	System.out.println("found graphemes: " + Arrays.toString(foundGraphemes));
                }
                int xAlignLen = knownGraphemes.length + 1;
                int yAlignLen = foundGraphemes.length + 1;
                int[][] alignment = new int[xAlignLen][yAlignLen];
                
                // Initialize edges for alignment
                for (int i = 0; i < xAlignLen; i++) {
                    alignment[i][0] = 1 * -i;
                }
                for (int j = 0; j < yAlignLen; j++) {
                    alignment[0][j] = 1 * -j;
                }

                // Compute alignment matrix
                for (int i = 1; i < xAlignLen; i++) {
                    for(int j = 1; j < yAlignLen; j++) {
                        int match  = alignment[i - 1][j - 1] + similarity(knownGraphemes[i-1], foundGraphemes[j-1], sim);
                        int delete = alignment[i - 1][j] + linGap();
                        int insert = alignment[i][j - 1] + linGap();
                        alignment[i][j] = max(match, delete, insert);
                        
                        
                    }
                }
                int finalMatch = alignment[xAlignLen -1][yAlignLen -1];
                
                

                int bestValue = 0;
                for (int i = 0; i < knownGraphemes.length; i++){
                	bestValue += similarity(knownGraphemes[i], knownGraphemes[i], sim);
                }

                int worstVal = 0;
                for (int i = 0; i < knownGraphemes.length; i++){
                	worstVal += sim.getMin(knownGraphemes[i]);
                }
                for (int i = 0; i < foundGraphemes.length - knownGraphemes.length; i++){
                	worstVal += linGap();
                }
            	
            	//if finalMatch is the same as bestValue, we get 1. as finalMatch gets closer
                //to the worst possible value, the top gets closer and closer to 0, making the confidence
                //go to 0
                int denom = Math.abs(worstVal - bestValue);
                
                indx[0] = allFoundGraphemes.length;
                // NOTE: to future groups:  distance is not necessarily relevant here, 
                // it would require calculating edit distance as well as NW alignments
            	dist[0] = Math.abs(worstVal - finalMatch);
                
                
                if(denom == 0) {
                	conf[0] = 100.0; // * ((SimilarityMatrix.maxValue - bestValue)/SimilarityMatrix.maxValue);
                	System.out.println("Somehow, denominator is 0. Reporting confidence of " + conf[0]);
                } else {
                	conf[0] = (100.0 * Math.abs(worstVal - finalMatch) / denom) ;
                }
                // Penalize bad confidences
                if (conf[0] > 100.0) {
                	conf[0] = 100; // - (conf[0] - 100);
//                	if(conf[0] < 0) {
//                		conf[0] = 0.0F;
                	}
//                	//System.out.println("Greater than 100 confidence, penalizing. New conf = " + conf[0]);
//                }
                
                
            	
                
                if (debug) {
                	String Alignment = "[ [ _ ";
                	for (int i = 0; i < foundGraphemes.length; i++){
                		Alignment += foundGraphemes[i] + " ";
                	}
                	Alignment += "]\n";
                	for (int i = 0; i < xAlignLen; i++) {
                		if (i == 0){
                			Alignment +="_ [ ";
                		}
                		else{
                			Alignment += knownGraphemes[i-1] + " [ ";
                		}
                		
                        for(int j = 0; j < yAlignLen; j++) {
                        	Alignment += alignment[i][j] + " ";
                        }
                        Alignment += "]\n";
                	}
                	Alignment += "]";
                	System.out.println("Alignment matrix:\n" + Alignment);
                	
                	
    	            String[] optAligns = constructAlignment(alignment, knownGraphemes, foundGraphemes, sim);
    	            
    	            System.out.printf("aligned\n%s and %s as\n%s\n%s\n%s\nindels: %s\n", 
    	            		joinAlignment(knownGraphemes), joinAlignment(foundGraphemes), 
    	            		optAligns[0], optAligns[1], optAligns[2], optAligns[3]);
                }
        }
    
    
    public static void compareWCutoff(String known, String[] allFoundGraphemes, 
            final int foundStart, double[] conf, int[] indx, int[] dist, SimilarityMatrix sim) {

                // Split string into graphemes
                String[] knownGraphemes = known.split("- |-| ");
                if (debug){
                	System.out.println("known graphemes: " + Arrays.toString(knownGraphemes));
                }
                String[] foundGraphemes = Arrays.copyOfRange(allFoundGraphemes, foundStart, allFoundGraphemes.length);
                if (debug){
                	System.out.println("found graphemes: " + Arrays.toString(foundGraphemes));
                }
                int xAlignLen = knownGraphemes.length + 1;
                int yAlignLen = foundGraphemes.length + 1;
                int[][] alignment = new int[xAlignLen][yAlignLen];
                
                // Initialize edges for alignment
                for (int i = 0; i < xAlignLen; i++) {
                    alignment[i][0] = 1 * -i;
                }
                for (int j = 0; j < yAlignLen; j++) {
                    alignment[0][j] = 1 * -j;
                }

                // Compute alignment matrix
                for (int i = 1; i < xAlignLen; i++) {
                    for(int j = 1; j < yAlignLen; j++) {
                        int match  = alignment[i - 1][j - 1] + similarity(knownGraphemes[i-1], foundGraphemes[j-1], sim);
                        int delete = alignment[i - 1][j] + linGap();
                        int insert = alignment[i][j - 1] + linGap();
                        alignment[i][j] = max(match, delete, insert);
                        
                        
                    }
                }
                int finalMatch = alignment[xAlignLen -1][yAlignLen -1];
                
                

                int bestValue = 0;
                for (int i = 0; i < knownGraphemes.length; i++){
                	bestValue += similarity(knownGraphemes[i], knownGraphemes[i], sim);
                }

                int worstVal = 0;
                for (int i = 0; i < knownGraphemes.length; i++){
                	worstVal += sim.getMin(knownGraphemes[i]);
                }
                for (int i = 0; i < foundGraphemes.length - knownGraphemes.length; i++){
                	worstVal += linGap();
                }
            	
            	//if finalMatch is the same as bestValue, we get 1. as finalMatch gets closer
                //to the worst possible value, the top gets closer and closer to 0, making the confidence
                //go to 0
                int denom = Math.abs(worstVal - bestValue);
                // If our denominator is 0, we know that worstVal == finalMatch anyways, so we avoid
                // The division by zero and still get good-ish data //TODO: determine (1) what best == worst implies and 
                // (2) if that means the worst match is just really good, or if the best match is really bad. 
                
                //System.out.printf("Best val %d |worst val %d |finalMatch %d\n", bestValue, worstVal, finalMatch); 
                
                int bestIndex = 0;
            	for (int i = 0; i < allFoundGraphemes.length; ++i) {
            		if (alignment[knownGraphemes.length][i] <= bestValue) {
                        bestIndex = i;
                        bestValue = alignment[knownGraphemes.length][i];
                    }
            	}
                indx[0] = bestIndex;
                // NOTE: to future groups:  distance is not necessarily relevant here, 
                // it would require calculating edit distance as well as NW alignments
            	dist[0] = Math.abs(worstVal - finalMatch);
                
                
                if(denom == 0) {
                	conf[0] = 100.0; // * ((SimilarityMatrix.maxValue - bestValue)/SimilarityMatrix.maxValue);
                	System.out.println("Somehow, denominator is 0. Reporting confidence of " + conf[0]);
                } else {
                	conf[0] = (100.0 * Math.abs(worstVal - finalMatch) / denom) ;
                }
                // Penalize bad confidences
                if (conf[0] > 100.0) {
                	conf[0] = 100; // - (conf[0] - 100);
//                	if(conf[0] < 0) {
//                		conf[0] = 0.0F;
                	}
//                	//System.out.println("Greater than 100 confidence, penalizing. New conf = " + conf[0]);
//                }
                
                
            	
                
                if (debug) {
                	String Alignment = "[ [ _ ";
                	for (int i = 0; i < foundGraphemes.length; i++){
                		Alignment += foundGraphemes[i] + " ";
                	}
                	Alignment += "]\n";
                	for (int i = 0; i < xAlignLen; i++) {
                		if (i == 0){
                			Alignment +="_ [ ";
                		}
                		else{
                			Alignment += knownGraphemes[i-1] + " [ ";
                		}
                		
                        for(int j = 0; j < yAlignLen; j++) {
                        	Alignment += alignment[i][j] + " ";
                        }
                        Alignment += "]\n";
                	}
                	Alignment += "]";
                	System.out.println("Alignment matrix:\n" + Alignment);
                	
                	
    	            String[] optAligns = constructAlignment(alignment, knownGraphemes, foundGraphemes, sim);
    	            
    	            System.out.printf("aligned\n%s and %s as\n%s\n%s\n%s\nindels: %s\n", 
    	            		joinAlignment(knownGraphemes), joinAlignment(foundGraphemes), 
    	            		optAligns[0], optAligns[1], optAligns[2], optAligns[3]);
                }
        }
    
    //TODO: this is a test method, change this before you put this into "Production"
    public static void setSimilarityMatrix(cuneiform.Citizen cit)
    {
    	simMat = cit.personalMatrix;
    	hasMatrix = true;
    }
    
    private static String joinAlignment(String[] graphemes) {
    	StringBuilder alignment = new StringBuilder();
    	for (String g : graphemes) {
    		alignment.append(g + " ");
    	}
    	return alignment.toString();
    }
    
    public static String[] constructAlignment(int[][] alignMatrix, String[] known, String[] unknown) {
    	//TODO: error checking
    	int i = alignMatrix.length-1;
    	int j = alignMatrix[0].length-1;
    	StringBuilder knownAlign = new StringBuilder(); 
    	StringBuilder foundAlign = new StringBuilder();
    	StringBuilder myversion = new StringBuilder();
    	int indel = 0;
    	
    	while (i > 0 && j > 0) {
    		if ( (alignMatrix[i][j] == (alignMatrix[i - 1][j - 1] + similarity(known[i-1], unknown[j-1]))))
    		{
    			myversion.insert(0, known[i-1] + " ");
    			knownAlign.insert(0, known[i-1] + " ");
    			foundAlign.insert(0, unknown[j-1] + " ");
    			i--;
    			j--;
    		} else if (alignMatrix[i][j] == (alignMatrix[i - 1][j] + linGap())) {
    			myversion.insert(0," - ");
    			knownAlign.insert(0, known[i-1] + " ");
    			foundAlign.insert(0, "- ");
    			indel++;
    			i--;
    		} else if (alignMatrix[i][j] == (alignMatrix[i][j - 1] + linGap())) {
    			myversion.insert(0, " _ ");
    			knownAlign.insert(0, "- ");
    			foundAlign.insert(0, unknown[j-1] + " ");
    			indel++;
    			j--;
    		}
    	}
    	
    	return new String[]{knownAlign.toString(), foundAlign.toString(), myversion.toString(), Integer.toString(indel)};
    }
    
    public static String[] constructAlignment(int[][] alignMatrix, String[] known, String[] unknown, SimilarityMatrix sim) {
    	int i = alignMatrix.length-1;
    	int j = alignMatrix[0].length-1;
    	StringBuilder knownAlign = new StringBuilder(); 
    	StringBuilder foundAlign = new StringBuilder();
    	StringBuilder myversion = new StringBuilder();
    	int indel = 0;
    	
    	while (i > 0 && j > 0) {
    		if ( (alignMatrix[i][j] == (alignMatrix[i - 1][j - 1] + similarity(known[i-1], unknown[j-1], sim))))
    		{
    			myversion.insert(0, known[i-1] + " ");
    			knownAlign.insert(0, known[i-1] + " ");
    			foundAlign.insert(0, unknown[j-1] + " ");
    			i--;
    			j--;
    		} else if (alignMatrix[i][j] == (alignMatrix[i - 1][j] + linGap())) {
    			myversion.insert(0," - ");
    			knownAlign.insert(0, known[i-1] + " ");
    			foundAlign.insert(0, "- ");
    			indel++;
    			i--;
    		} else if (alignMatrix[i][j] == (alignMatrix[i][j - 1] + linGap())) {
    			myversion.insert(0, " _ ");
    			knownAlign.insert(0, "- ");
    			foundAlign.insert(0, unknown[j-1] + " ");
    			indel++;
    			j--;
    		}
    	}
    	
    	return new String[]{knownAlign.toString(), foundAlign.toString(), myversion.toString(), Integer.toString(indel)};
    }
    
    // Each string represents a grapheme
    static int getCost(String c1, String c2) {
        c1 = c1.replace("<>[]", "");
        c2 = c2.replace("<>[]", "");
        int match = 1;
        int mismatch = -1;
        // If both are empty, 
        if (c1.isEmpty() ^ c2.isEmpty()) {
            return mismatch;
        }
        if (c1.equalsIgnoreCase(c2)) {
            return match; // Match
        } else if (c1.equalsIgnoreCase("{d}" + c2) || c2.equalsIgnoreCase("{d}" + c1)) {
            return match; //Divine name
        } else if (c1.equalsIgnoreCase(c2 + "{ki}") || c2.equalsIgnoreCase(c1 + "{ki}")) {
            return match;
        } else if (c1.equalsIgnoreCase(c2 + "(disz)") || c2.equalsIgnoreCase(c1 + "(disz)")) {
            return match;
        } else if (c1.equalsIgnoreCase(c2 + "#") || c2.equalsIgnoreCase(c1 + "#")) {
            return match;
        } else if (c1.equalsIgnoreCase("{d}en.zu") && c2.equalsIgnoreCase("{d}suen")) {
            return match; // {d}en.zu and {d}suen are equivalent
        } else if (c1.equalsIgnoreCase("{d}suen") && c2.equalsIgnoreCase("{d}en.zu")) {
            return match; // {d}en.zu and {d}suen are equivalent
        } else {
            return mismatch;
        }
    }
    
    private static int max(int a, int b, int c) {
      int mab = (a > b) ? a : b;
      return (mab > c) ? mab : c;
    }

    private static int similarity(String graphemeA, String graphemeB) {
        // For now, ignore input and return 0, for edit distance emulation
    	int cost = getCost(graphemeA, graphemeB);
    	if (hasMatrix) {
    		try {
    			cost += simMat.score(graphemeA, graphemeB);
    		} catch (Exception e) {
    			System.out.println("No Similarity matrix was found.");
    		}
    	}
    	return cost;
    }
    
    private static int similarity(String graphemeA, String graphemeB, SimilarityMatrix sim) {
        // For now, ignore input and return 0, for edit distance emulation
    	int cost = 0;// = getCost(graphemeA, graphemeB);
		try {
			cost = sim.score(graphemeA, graphemeB);
		} catch (Exception e) {
			System.out.println("caught exception getting a score, investigate");
			e.printStackTrace();
			
			if (graphemeA != null && graphemeB != null) {
				cost = getCost(graphemeA, graphemeB);
			}
		}
    	return cost;
    }

    private static int linGap() {
        return -1;
    }
}



