package matrixComparisons;

import genetics.GeneticServer;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import cuneiform.Citizen;
import cuneiform.DateExtractor;
import cuneiform.FoundDateList;
import cuneiform.KnownDate;
import cuneiform.Parser;
import cuneiform.stringComparator.SimilarityMatrix;

//by Danielle Thurow
//takes the filepath for a similarity matrix
//reads it in
//then gives us the confidence that it gets 
//and how often it's actually correct


public class MatrixCompare {

	
	private static FoundDateList foundDateList;
	private static Connection dbConn;
	
	
	
	
	
	public MatrixCompare() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
//		if (args.length < 1){
//			System.out.println("We need a filepath for the matrix");
//			System.exit(-1);
//		}
		
		//get the known dates and found dates
		List<KnownDate> allKnownDates = null;
		try {
			registerMySqlDriver();
			dbConn = DriverManager.getConnection(Parser.dbHost, Parser.dbUser, Parser.dbPass);
			System.out.println("DB connection secured.");
			allKnownDates = DateExtractor.readKnownYears(dbConn);
			System.out.println("KnownYears read.");
		} catch (SQLException e) {
			System.out.println("couldn't connect to database. Dying.");
			e.printStackTrace();
			 System.exit(-1);
		}
		
		try {
			foundDateList = new FoundDateList(dbConn);
		} catch (SQLException e) {
			System.out.println("tried to get found dates, failed miserably. Dying.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		SimilarityMatrix simMat = new SimilarityMatrix();
		//simMat.specialRandomize();
		try {
			simMat.readMatrix("data/matrixTest.txt");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		try {
//			simMat.readMatrix(args[0]);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		//create a citzen with the matrix that was passed in
		Citizen cit = new Citizen(1, simMat);
		
		//call genetic server, to get it up and running
		GeneticServer server =  new GeneticServer(1, 2);
		server.setAllKnownDates(allKnownDates);
		try {
			server.live(cit, foundDateList.getFoundDates());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(0);

	}
	
	
	private static void registerMySqlDriver()
    {
    	/*
    	 * 	Steps to register MySQL JDBC under Ubuntu:
    	 * 		1. sudo apt-get install libmysql-java
    	 * 		2. Ensure that /usr/share/java/mysql.jar exists.
    	 * 		3. Register external .jar with your IDE.
    	 * 			In Eclipse:
    	 * 			Project --> Properties --> Java Build Path
    	 * 				--> Add External JARs...
    	 */
    	try
    	{
    		// Register the MySQL JDBC driver.
    		
    		Class.forName("com.mysql.jdbc.Driver");
    	}
    	catch (ClassNotFoundException e)
    	{
    		// The MySQL Java connector does not appear to be installed.
    		// There's not much we can do about that !
    		
    		System.err.println("Failed to register the JDBC driver.");
    		e.printStackTrace();
    	}
    } 
	

}
