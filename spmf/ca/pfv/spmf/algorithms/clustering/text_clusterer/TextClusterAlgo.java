package ca.pfv.spmf.algorithms.clustering.text_clusterer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.pfv.spmf.tools.MemoryLogger;

/* This file is copyright (c) 2008-2012 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * @author Sabarish Raghu
 * ClusterAlgo is an implementation of text clustering algorithm.
 * 
 * Input is of TSV format which is of format RecordId \t Record
 * Eg: 1	The document about a cat
 * Output is of TSV format which has RecordId \t clusternumber 
 * */

public class TextClusterAlgo {
static HashSet<String> allWords=new HashSet<String>();
static HashMap<Integer, Integer> idMap=new HashMap<Integer, Integer>(); //map between the recordId and its corresponding index.
long startTimestamp = 0; // last execution start time
long endTimeStamp = 0;   // last execution end time
boolean stemFlag;		//stemming to be done or not
boolean stopWordFlag;	//stop words to be removed or not
/**
 * @param inputPath input file path
 * @param outputPat output file path
 * @param stemFlag
 * @param stopWordFlag
 */
public void runAlgorithm(String inputPath, String outputPath, boolean stemFlag, boolean stopWordFlag)
{
	this.stemFlag=stemFlag;
	this.stopWordFlag=stopWordFlag;
	runAlgorithm(inputPath, outputPath);
}
/**
 * @param inputPath input file path
 * @param outputPat output file path
 */
public void runAlgorithm(String inputPath, String outputPath)
{
	startTimestamp=System.currentTimeMillis();
	try
	{
	BufferedReader inputReader=new BufferedReader(new FileReader(new File(inputPath)));
	if(inputPath!=null)
	{
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File(outputPath)));
		ArrayList<Record> records=loadInput(inputReader,stemFlag,stopWordFlag);
		for(Record record:records)
		{
			double tfIdfVector[]=new double[allWords.size()];
			int vectorIncrementer=0;
			for(String word:allWords)
			{
				tfIdfVector[vectorIncrementer]=FindTFIDF(record.getAttribute(), word, records);
				vectorIncrementer++;
			}
			record.setTfVector(tfIdfVector);
		}
		double sim[][]=new double[records.size()][records.size()];
		for(int i=0;i<records.size();i++)
		{
			for(int j=0;j<records.size();j++)
			{
				
				sim[i][j]=calculateSimilarity(records.get(i).getTfVector(),records.get(j).getTfVector());
			
			}
		}
		ArrayList<SimilarRecords> similarRecordPairs=new ArrayList<SimilarRecords>();
		for(int i=0;i<records.size();i++)
		{
			double max=0.0;int ipos=0;int jpos=0;
			for(int j=0;j<records.size();j++)
			{
				if(i!=j)
				{
				if(sim[i][j]>max)
				{
					max=sim[i][j];
					ipos=i;
					jpos=j;
				}
				}
			}
			SimilarRecords pair=new SimilarRecords();
			pair.setRecord1Pos(ipos);
			pair.setRecord2Pos(jpos);
			pair.setSimilarity(max);
			similarRecordPairs.add(pair);
		}
		Set<TextCluster> clusters=new HashSet<TextCluster>();
		for(SimilarRecords similarPair:similarRecordPairs)
		{
			int i=similarPair.getRecord1Pos();
			int j=similarPair.getRecord2Pos();
			ArrayList<Integer> tempList=new ArrayList<Integer>();
			TextCluster result=new TextCluster();
			tempList.add(i);
			tempList.add(j);
			result.setCluster(tempList);
			clusters.add(result);
		}
		Set<TextCluster> clusterSet=new HashSet<TextCluster>(clusters);
	 	Iterator<TextCluster> clusterIterator=clusterSet.iterator();
		int clusterNum=0;
		outputWriter.write("RecordId\tClusternum\n");
		while(clusterIterator.hasNext())
		{
			TextCluster output=(TextCluster) clusterIterator.next();
			ArrayList<Integer> list=output.getCluster();
			for(int i=0;i<list.size();i++)
			{
				outputWriter.write(idMap.get(list.get(i))+"\t"+clusterNum+"\n");
			}
			clusterNum++;
		}
		outputWriter.close();
		endTimeStamp=System.currentTimeMillis();
	}
	else
	{
		System.out.println("Please pass the path of the input");
	}
	}
	catch(Exception e)
	{
		System.out.println("Either file didn't exist or error while clustering");
		e.printStackTrace();
	}
	}
/**
 * Print statistics of the latest execution to System.out.
 */
public void printStatistics() {
	System.out.println("========== KMEANS - STATS ============");
	System.out.println(" Total time ~: " + (endTimeStamp - startTimestamp)
			+ " ms");
	System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " mb ");
	System.out.println("=====================================");
}

/**
 * Calculates the similarity between two documents by calculation between the vectors of the corresponding documents.
 * @param tfIdfVector1 tfIdf value of record 1
 * @param tfIdfVector2 tfIdf value of record 2
 * @return similarity value between the record's vectors
 */
private static double calculateSimilarity(double[] tfIdfVector1,
			double[] tfIdfVector2) {
		// TODO Auto-generated method stub
   	double similarity=0;
   	for(int i=0;i<tfIdfVector1.length;i++){
   		similarity+=tfIdfVector1[i]*tfIdfVector2[i];
   	}
   	return similarity;

	}
/**
 * load the input as objects of records
 * @param inputReader the reader object to read input
 * @param stemFlag if true, do the stemming; else, do not stem.
 * @param stopWordFlag if true, do the stop word removal; else, do not remove stop words.
 * @return the list of records
 */
private static ArrayList<Record> loadInput(BufferedReader inputReader, boolean stemFlag, boolean stopWordFlag) {
	// TODO Auto-generated method stub
	ArrayList<Record> records=new ArrayList<Record>();
	String currentLine;
	String[] line;
	int recordId;
	String words[];
	try
	{
		int i=0;
		while((currentLine=inputReader.readLine())!=null)
		{
			line=currentLine.split("\t",-1);
			Record record=new Record();
			recordId=Integer.parseInt(line[0]);
			record.setRecordId(recordId);
			String attribute=line[1].toLowerCase();
			attribute=attribute.replaceAll("[^a-zA-Z0-9]+"," ");
			if(stopWordFlag==true)
			{
				StopWordAnalyzer analyzer=new StopWordAnalyzer();
				attribute=analyzer.removeStopWords(attribute);
			}
			idMap.put(i, recordId);
			if(stemFlag==true)	
			{
				PorterStemmer stemmer=new PorterStemmer();
				attribute=stemmer.stem(attribute);
			}
			words=attribute.split(" ");
			for(String word:words)
			{
				allWords.add(word);
			}
			record.setAttribute(attribute);
			records.add(record);
			i++;
		}
		return records;
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return records;
}
/** To find the TFIDF value for a given document and a given term in the whole set of documents
 * @param document The Text record in the input file.
 * @param term The term in the allWords
 * @param records The whole record collection of the input file.
 * @return tfidf value of the given document and the term
 */
private static double FindTFIDF(String document, String term, ArrayList<Record> records)
{
   	double tf = FindTermFrequency(document, term);
    float idf = FindInverseDocumentFrequency(term,records);
    return tf * idf;
}
/**
 * To find the  no. of document that contains the term in whole document collection
 * i.e.; log of the ratio of  total no of document in the collection to the no. of document containing the term
 * we can also use Math.Log(occurance/(1+documentCollection.size)) to deal with divide by zero case; 
 *@param term The term in the allWords
 *@param records The whole record collection of the input file.
 *@return the inverse document frequency
 */

private static float FindInverseDocumentFrequency(String term,
		ArrayList<Record> records) {
	   int occurance=0;
	   for(Record record:records)
	   {
		   if(record.getAttribute().contains(term))
		   {
			   occurance++;
		   }
	   }
	    return (float)Math.log((float)occurance / (1+(float)records.size()));
	}

/**
 * To find the ratio of no of occurance of term t in document d to the total no of terms in the document
 *@param document The Text record in the input file
 *@param term The term in the allWords
 *@return the term frequency of term in the document
 */
private static double FindTermFrequency(String document, String term) {
	
	   int occurance=0;
	   String[] words=document.split(" ");
	   for(String word:words)
	   {
		   if(word.equalsIgnoreCase(term))
		   {
			   occurance++;
		   }
	   }
	   return (double)((float)occurance / (float)(words.length));
}

}
