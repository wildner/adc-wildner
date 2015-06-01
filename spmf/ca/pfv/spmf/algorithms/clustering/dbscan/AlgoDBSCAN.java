package ca.pfv.spmf.algorithms.clustering.dbscan;
/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceEuclidian;
import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceFunction;
import ca.pfv.spmf.datastructures.kdtree.KDTree;
import ca.pfv.spmf.patterns.cluster.Cluster;
import ca.pfv.spmf.patterns.cluster.DoubleArray;
import ca.pfv.spmf.tools.MemoryLogger;

/* This file is copyright (c) 2008-2015 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * An implementation of the DBSCAN algorithm (Ester et al., 1996). 
 * Note that original algorithm suggested using a R*-tree to index points 
 * to avoid having a O(n^2) complexity, but we instead used a KD-Tree.
 * The DBScan algorithm was originally published in:
 * <br/><br/>
 * 
 * Ester, Martin; Kriegel, Hans-Peter; Sander, Jörg; Xu, Xiaowei (1996). Simoudis, Evangelos; 
 * Han, Jiawei; Fayyad, Usama M., eds. A density-based algorithm for discovering clusters in 
 * large spatial databases with noise. Proceedings of the Second International Conference on Knowledge
 *  Discovery and Data Mining (KDD-96). AAAI Press. pp. 226–231.
 * 
 * @author Philippe Fournier-Viger
 */

public class AlgoDBSCAN {

	// The list of clusters generated
	protected List<Cluster> clusters = null;

	// For statistics
	protected long startTimestamp; // the start time of the latest execution
	protected long endTimestamp;  // the end time of the latest execution
	long numberOfNoisePoints; // the number of iterations that was performed
	
	/* The distance function to be used for clustering */
	DistanceFunction distanceFunction = new DistanceEuclidian(); 
	
	/* This KD-Tree is used to index the data points for fast access to points in the epsilon radius*/
	KDTree kdtree;

	/**
	 * Default constructor
	 */
	public AlgoDBSCAN() { 
		
	}
	
	/**
	 * Run the DBSCAN algorithm
	 * @param inputFile an input file path containing a list of vectors of double values
	 * @param minPts  the minimum number of points (see DBScan article)
	 * @param epsilon  the epsilon distance (see DBScan article)
	 * @param seaparator  the string that is used to separate double values on each line of the input file (default: single space)
	 * @return a list of clusters (some of them may be empty)
	 * @throws IOException exception if an error while writing the file occurs
	 */
	public List<Cluster> runAlgorithm(String inputFile, int minPts, double epsilon, String separator) throws NumberFormatException, IOException {
		
		// record the start time
		startTimestamp =  System.currentTimeMillis();
		// reset the number of noise points to 0
		numberOfNoisePoints =0;
		
		// Structure to store the vectors from the file
		List<DoubleArray> points = new ArrayList<DoubleArray>();
		
		// read the vectors from the input file
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		String line;
		// for each line until the end of the file
		while (((line = reader.readLine()) != null)) {
			// if the line is  a comment, is  empty or is a
			// kind of metadata
			if (line.isEmpty() == true ||
					line.charAt(0) == '#' || line.charAt(0) == '%'
							|| line.charAt(0) == '@') {
				continue;
			}
			// split the line by spaces
			String[] lineSplited = line.split(separator);
			// create a vector of double
			double [] vector = new double[lineSplited.length];
			// for each value of the current line
			for (int i=0; i< lineSplited.length; i++) { 
				// convert to double
				double value = Double.parseDouble(lineSplited[i]);
				// add the value to the current vector
				vector[i] = value;
			}
			// add the vector to the list of vectors
			points.add(new DoubleArrayDBS(vector));
		}
		// close the file
		reader.close();
		
		// build kd-tree
		kdtree = new KDTree();
		kdtree.buildtree(points);
		
		// For debugging, you can print the KD-Tree by uncommenting the following line:
//		System.out.println(kdtree.toString());
		
		// Create a single cluster and return it 
		clusters = new ArrayList<Cluster>();
		
		
		// For each point in the dataset
		for(DoubleArray point: points) {
			// if the node is already visited, we skip it
			DoubleArrayDBS pointDBS = (DoubleArrayDBS) point;
			if(pointDBS.visited == true) {
				continue;
			}
			
			// mark the point as visited
			pointDBS.visited = true;
			
			// find the neighboors of this point
			List<DoubleArray> neighboors = kdtree.pointsWithinRadiusOf(pointDBS, epsilon);
			// if it is not noise
			if(neighboors.size() >= minPts -1) { // - 1 because we don't count the point itself in its neighborood
				
				// create a new cluster
				Cluster cluster = new Cluster();
				clusters.add(cluster);
				
				// transitively add all points that can be reached
				expandCluster(pointDBS, neighboors, cluster, epsilon, minPts);
			}else {
				// it is noise
				numberOfNoisePoints++;
			}
			
		}
		
		kdtree = null;
		
		// check memory usage
		MemoryLogger.getInstance().checkMemory();
		
		// record end time
		endTimestamp =  System.currentTimeMillis();
		
		// return the clusters
		return clusters;
	}

	/**
	 * The DBScan expandCluster() method
	 * @param currentPoint the current point
	 * @param neighboors the neighboors of the current point
	 * @param cluster the current cluster
	 * @param epsilon the epsilon parameter
	 * @param minPts the minPts parameter
	 */
	private void expandCluster(DoubleArrayDBS currentPoint,
			List<DoubleArray> neighboors, Cluster cluster, double epsilon, int minPts) {	
		// add the current point to the cluster
		cluster.addVector(currentPoint);
		
		// for each neighboor
		for(DoubleArray newPoint: neighboors) {
			DoubleArrayDBS newPointDBS = (DoubleArrayDBS) newPoint;
			
			// if this point has not been visited yet
			if(newPointDBS.visited == false) {
					
				// mark the point as visited
				newPointDBS.visited = true;
				
				// find the neighboors of this point
				List<DoubleArray> newNeighboors = kdtree.pointsWithinRadiusOf(newPointDBS, epsilon);
				
				// if this point is not noise
				if(newNeighboors.size() >= minPts - 1) { // - 1 because we don't count the point itself in its neighborood
					expandCluster(newPointDBS, newNeighboors, cluster, epsilon, minPts);
				}else {
					// it is noise
					numberOfNoisePoints++;
				}
			}
		}

		// check memory usage
		MemoryLogger.getInstance().checkMemory();
	}

	/**
	 * Save the clusters to an output file
	 * @param output the output file path
	 * @throws IOException exception if there is some writing error.
	 */
	public void saveToFile(String output) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		// for each cluster
		for(int i=0; i< clusters.size(); i++){
			// if the cluster is not empty
			if(clusters.get(i).getVectors().size() >= 1){
				// write the cluster
				writer.write(clusters.get(i).toString());
				// if not the last cluster, add a line return
				if(i < clusters.size()-1){
					writer.newLine();
				}
			}
		}
		// close the file
		writer.close();
	}
	
	/**
	 * Print statistics of the latest execution to System.out.
	 */
	public void printStatistics() {
		System.out.println("========== DBSCAN - STATS ============");
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp)
				+ " ms");
		System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " mb ");
		System.out.println(" Number of noise points: " + numberOfNoisePoints);
		System.out.println("=====================================");
	}

}
