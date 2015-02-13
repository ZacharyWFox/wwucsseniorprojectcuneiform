package cuneiform.stringComparator;

import java.util.Arrays;

public class SumerianNWSubstringComparator {
    
    private static int [][] scoringMatrix;
    private static int divineBonus = 10;
    static boolean debug = true; // If true, print debug messages in this class
    // foundStart is an offset of the tablet text. fix this if we can.
    // 
    public static void compare(String known, String[] allFoundGraphemes, 
        final int foundStart, double[] conf, int[] indx, int[] dist) {
            // Split string into graphemes
            String[] knownGraphemes = known.split("-| ");
            String[] foundGraphemes = Arrays.copyOfRange(allFoundGraphemes, foundStart, allFoundGraphemes.length);
            
            int[][] alignment = new int[knownGraphemes.length][foundGraphemes.length];
            
            // Initialize edges for alignment
            for (int i = 0; i < knownGraphemes.length; i++) {
                alignment[i][0] = linGap() * i;
            }
            for (int j = 0; j < foundGraphemes.length; j++) {
                alignment[0][j] = linGap() * j;
            }

            // Compute alignment matrix
            for (int i = 1; i < knownGraphemes.length; i++) {
                for(int j = 1; j < foundGraphemes.length; j++) {
                    int match  = alignment[i - 1][j - 1] + similarity(knownGraphemes[i], foundGraphemes[j]);
                    int delete = alignment[i - 1][j] + linGap();
                    int insert = alignment[i][j - 1] + linGap();
                    alignment[i][j] = max(match, delete, insert);
                }
            }
            int bestValue = alignment[knownGraphemes.length-1][foundGraphemes.length-1];
            if (bestValue > knownGraphemes.length)
            	//System.out.print("stuff went wrong\n");
            conf[0] = 100.0 * Math.abs(knownGraphemes.length - bestValue) / knownGraphemes.length;
            indx[0] = foundGraphemes.length;
            dist[0] = bestValue;
            if (debug) {
	            String[] optAligns = constructAlignment(alignment, knownGraphemes, foundGraphemes);
	            
	            System.out.printf("aligned\n%s and %s as\n%s\n%s\n", 
	            		joinAlignment(knownGraphemes), joinAlignment(foundGraphemes), 
	            		optAligns[0], optAligns[1]);
            }
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
    	int i = known.length - 1;
    	int j = unknown.length - 1;
    	StringBuilder knownAlign = new StringBuilder(); 
    	StringBuilder foundAlign = new StringBuilder();
    	
    	while (i > 0 || j > 0) {
    		if ( i > 0 && j > 0 && (alignMatrix[i][j] == (alignMatrix[i - 1][j - 1] + similarity(known[i], unknown[j]))))
    		{
    			knownAlign.insert(0, known[i] + " ");
    			foundAlign.insert(0, unknown[j] + " ");
    			i--;
    			j--;
    		} else if (i > 0 && alignMatrix[i][j] == (alignMatrix[i - 1][j] + linGap())) {
    			knownAlign.insert(0, known[i] + " ");
    			foundAlign.insert(0, "- ");
    			i--;
    		} else if (j > 0 && alignMatrix[i][j] == (alignMatrix[i][j - 1] + linGap())) {
    			knownAlign.insert(0, "- ");
    			foundAlign.insert(0, unknown[j] + " ");
    			j--;
    		}
    	}
    	
    	return new String[]{knownAlign.toString(), foundAlign.toString()};
    }
    
    // Each string represents a grapheme
    static int getCost(String c1, String c2) {
        c1 = c1.replace("<>[]", "");
        c2 = c2.replace("<>[]", "");
        // If both are empty, 
        if (c1.isEmpty() ^ c2.isEmpty()) {
            return 0;
        }
        if (c1.equalsIgnoreCase(c2)) {
        	String lowerC = c1.toLowerCase();
        	// If we detect a positive metadata bonus
        	if (lowerC.startsWith("{d}"))
        		return divineBonus;
        	if (lowerC.endsWith("{ki}"))
        		return 5;//TODO: add a bonus for ki matches?;
        	//TODO: more
            return 1; // Match TODO: return similarity score
        } else if (c1.equalsIgnoreCase("{d}" + c2) || c2.equalsIgnoreCase("{d}" + c1)) {
            return 1; //Divine name
        } else if (c1.equalsIgnoreCase(c2 + "{ki}") || c2.equalsIgnoreCase(c1 + "{ki}")) {
            return 1;
        } else if (c1.equalsIgnoreCase(c2 + "(disz)") || c2.equalsIgnoreCase(c1 + "(disz)")) {
            return 1;
        } else if (c1.equalsIgnoreCase(c2 + "#") || c2.equalsIgnoreCase(c1 + "#")) {
            return 1;
        } else if (c1.equalsIgnoreCase("{d}en.zu") && c2.equalsIgnoreCase("{d}suen")) {
            return 1; // {d}en.zu and {d}suen are equivalent
        } else if (c1.equalsIgnoreCase("{d}suen") && c2.equalsIgnoreCase("{d}en.zu")) {
            return 1; // {d}en.zu and {d}suen are equivalent
        } else {
            return 0;
        }
    }
    
    private static int max(int a, int b, int c) {
      int mab = (a > b) ? a : b;
      return (mab > c) ? mab : c;
    }

    private static int similarity(String graphemeA, String graphemeB) {
        // For now, ignore input and return 0, for edit distance emulation
        return getCost(graphemeA, graphemeB);
    }

    private static int linGap() {
        return 1;
    }
}
