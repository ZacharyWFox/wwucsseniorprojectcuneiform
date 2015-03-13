package cuneiform;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import cuneiform.stringComparator.Confidence;


public class FoundDateList {

	private final List<FoundDate> foundDates;
	private static int sample_size = 1000;
	private static double min_confidence = .99F;
	
	public FoundDateList(Connection conn) throws SQLException {
		foundDates = new ArrayList<FoundDate>();
		List<YearReference> yearRefList = readYearRefs(conn);
		createFoundDates(conn, yearRefList);
	}
	
	public List<FoundDate> getFoundDates() {
		return this.foundDates;

	}
	
	private List<YearReference> readYearRefs(Connection conn) throws SQLException {
        // Generate list of YearReference objects containing data for all
        // year_reference in the database
		List<YearReference> yearRefList = new ArrayList<YearReference>();
		
		try (Statement stmt = conn.createStatement()) {
			stmt.execute("SELECT * FROM `year_reference` WHERE confidence >= " 
					+ String.valueOf(min_confidence) + " ;");
			ResultSet rs = stmt.getResultSet();
			if (rs == null) {
				throw new IllegalStateException("Cannot get ResultSet");
			}
			while (rs.next()) {
				int id = rs.getInt("year_reference_id");
				int canonical_year_id = rs.getInt("canonical_year_id");
				String found_text = rs.getString("found_text");
				double confidence = rs.getDouble("confidence");
				
				YearReference curYearRef = new YearReference(id, canonical_year_id, found_text, confidence);
				yearRefList.add(curYearRef);
			}
		}
		return yearRefList;
	}
	
	private void createFoundDates(Connection conn, List<YearReference> yearRefList) {
		// Randomly trim the list of YearReferences until reaching sample_size
		long seed = 1234;
		Random rand = new Random(seed);
		while(yearRefList.size() > sample_size) {
			yearRefList.remove(rand.nextInt(yearRefList.size()));
		}
		
		for (YearReference yearRef : yearRefList) {
			// Get the associated canonical year
			String text = new String();
			try (Statement stmt = conn.createStatement()) {
				stmt.execute("SELECT `text` FROM `canonical_year` WHERE `canonical_year_id`=" 
						+ String.valueOf(yearRef.canonical_year_id) + ";");
				ResultSet rs = stmt.getResultSet();
				
				if (rs != null) {
					rs.next();
					text = rs.getString("text");
				} else {
					text = null;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Create known date using canonical year
			KnownDate newKnownDate = new KnownDate(
					yearRef.id,
					text);
			
			Confidence newConfidence = new Confidence(
					0, // Distance - not in the DB
					yearRef.confidence);
			
			FoundDate newFoundDate = new FoundDate(
					newKnownDate,
					yearRef.found_text,
					newConfidence);
			
			foundDates.add(newFoundDate);
		}
	}
	
	private class YearReference {
		public int id;
		public int canonical_year_id;
		public String found_text;
		public double confidence;
		
		public YearReference(int id, int canonical_year_id, String found_text, double confidence) {
			this.id = id;
			this.canonical_year_id = canonical_year_id;
			this.found_text = found_text;
			this.confidence = confidence;
		}
	}
}
