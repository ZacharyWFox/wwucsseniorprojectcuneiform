package cuneiform.stringComparator;

import java.util.Arrays;

public class SumerianNWSubstringComparator {
    
    static int [][] scoringMatrix;
    // foundStart is an offset of the tablet text. fix this if we can.
    // 
    public static void compare(String known, String[] allFoundGraphemes, 
        final int foundStart, double[] conf, int[] indx, int[] dist) {
            // Split string into graphemes
            String[] knownGraphemes = known.split("-| ");
            String[] foundGraphemes = Arrays.copyOfRange(allFoundGraphemes, foundStart, allFoundGraphemes.length - 1);
            
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
    }		

 // Each string represents a grapheme
    static int getCost(String c1, String c2) {
        c1 = c1.replace("<>[]", "");
        c2 = c2.replace("<>[]", "");
        // If both are empty, 
        if (c1.isEmpty() ^ c2.isEmpty()) {
            return 1;
        }
        if (c1.equalsIgnoreCase(c2)) {
            return 0; // Match
        } else if (c1.equalsIgnoreCase("{d}" + c2) || c2.equalsIgnoreCase("{d}" + c1)) {
            return 0; //Divine name
        } else if (c1.equalsIgnoreCase(c2 + "{ki}") || c2.equalsIgnoreCase(c1 + "{ki}")) {
            return 0;
        } else if (c1.equalsIgnoreCase(c2 + "(disz)") || c2.equalsIgnoreCase(c1 + "(disz)")) {
            return 0;
        } else if (c1.equalsIgnoreCase(c2 + "#") || c2.equalsIgnoreCase(c1 + "#")) {
            return 0;
        } else if (c1.equalsIgnoreCase("{d}en.zu") && c2.equalsIgnoreCase("{d}suen")) {
            return 0; // {d}en.zu and {d}suen are equivalent
        } else if (c1.equalsIgnoreCase("{d}suen") && c2.equalsIgnoreCase("{d}en.zu")) {
            return 0; // {d}en.zu and {d}suen are equivalent
        } else {
            return 1;
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
