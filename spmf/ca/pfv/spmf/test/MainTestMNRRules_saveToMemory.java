package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.associationrules.MNRRules.AlgoMNRRules;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.AlgoZart;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.TZTableClosed;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.rule_itemset_array_integer_with_count.Rules;

/**
 *  Example of how to generate minimal non redundant
 *  association rules in source code.
 * 
 * @author Philippe Fournier-Viger, 2008
 */
public class MainTestMNRRules_saveToMemory{

	public static void main(String[] args) throws IOException {

		System.out.println("STEP 1 :  FIND CLOSED ITEMSETS AND MINIMUM GENERATORS By EXECUTING THE ZART ALGORITHM");
		String input = fileToPath("contextZart.txt");
		String output = ".//output.txt";
//		
		double minsup = 0.6;  // minimum support
		double minconf = 0.6; // minimum confidence
		
		TransactionDatabase database = new TransactionDatabase();
		try {
			database.loadFile(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		database.printDatabase();
		
		// Applying the Zart algorithm
		AlgoZart zart = new AlgoZart();
		TZTableClosed results = zart.runAlgorithm(database, minsup);
		//zart.printStatistics();
		
		// PRINT RESULTS FROM THE ZART ALGORITHM
//		int countClosed=0;
//		int countGenerators=0;
//		System.out.println("===================");
//		for(int i=0; i< results.levels.size(); i++){
//			System.out.println("LEVEL : " + i);
//			for(Itemset closed : results.levels.get(i)){
//				System.out.println(" CLOSED : " + closed.toString() + "  supp : " + closed.getAbsoluteSupport());
//				countClosed++;
//				System.out.println("   GENERATORS : ");
//						List<Itemset> generators = results.mapGenerators.get(closed);
//				// if there are some generators
//				if(generators.size()!=0) { 
//					for(Itemset generator : generators){
//						countGenerators++;
//						System.out.println("     =" + generator.toString());
//					}
//				}else {
//					// otherwise the closed itemset is a generator
//					countGenerators++;
//					System.out.println("     =" + closed.toString());
//                                }
//                        }
//                }										
		
		System.out.println("STEP 2 :Extract Rules from closed item set and associated generators by using MNR Rules ");

                // Run the algorithm to generate MNR rules
		AlgoMNRRules algoMNR = new AlgoMNRRules();
		algoMNR.runAlgorithm(output, minconf, results, database.size());
                algoMNR.printStatistics();
        //******************************  read large data set  ***************************************
//                TransactionDatabaseConverter converter = new TransactionDatabaseConverter();
//                String input = fileToPath("test.arff");
//                String input_converted=".//input_converted.txt";
//                String output = ".//output.txt";
//                String final_output =".//final_output.txt";
//                Map<Integer, String> mapping = converter.convertARFFandReturnMap(input,input_converted, Integer.MAX_VALUE);     
//                
//                double minsup = 0.2;  // minimum support
//		double minconf = 0.6; // minimum confidence
//		
//		TransactionDatabase database = new TransactionDatabase();
//		try {
//			database.loadFile(input_converted);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		database.printDatabase();
//                // Applying the Zart algorithm
//		AlgoZart zart = new AlgoZart();
//		TZTableClosed results = zart.runAlgorithm(database, minsup);
//		zart.printStatistics();
//                TFTableFrequent frequents = zart.getTableFrequent(); 
//                 zart.saveResultsToFile(output);
//                 //zart.saveResultsToFile("final_output.txt");
//                   
//                frequents.levels.size();
//                System.out.println("STEP 2 : CALCULATING MNR ASSOCIATION RULES");
//		// Run the algorithm to generate MNR rules               
//		AlgoMNRRules algoMNR = new AlgoMNRRules();      
//                algoMNR.runAlgorithm(output, minconf, results, database.size());
//                ResultConverter converter1 = new ResultConverter();
//                converter1.convert(mapping, output, final_output);
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestMNRRules_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
