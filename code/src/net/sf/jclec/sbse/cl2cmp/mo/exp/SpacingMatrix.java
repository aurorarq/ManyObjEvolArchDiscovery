package net.sf.jclec.sbse.cl2cmp.mo.exp;

import net.sf.jclec.JCLEC;

/**
 * Spacing indicator. Adapted from the 
 * implementation of MOEAFramework 
 * (http://www.moeaframework.org/). It
 * allows optimization problems with
 * n objectives (n>=2). It works with
 * the front stores in a matrix format.
 * 
 * @author Aurora Ramirez Quesada
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * 
 * @version 1.0
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * */
public class SpacingMatrix implements JCLEC{

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -957412979548708483L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public SpacingMatrix(){
		// Do nothing
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------- Public methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Calculate the spacing metric for a given front
	 * @param front: the pareto front
	 * @return Spacing value for the pareto front
	 * */
	public double calculateSpacing(double [][] front, int  noPoints) {
		if (noPoints < 2) {
			return 0.0;
		}
		
		double[] d = new double[noPoints];
		double [] ind1, ind2;
		
		for (int i = 0; i < noPoints; i++) {
			double min = Double.POSITIVE_INFINITY;
			ind1 = front[i];

			for (int j = 0; j < noPoints; j++) {
				if (i != j) {
					ind2 = front[j];
					min = Math.min(min, distance(ind1,ind2));
				}
			}

			d[i] = min;
		}

		double dsum = 0.0;
		for(int i=0; i<noPoints; i++)
			dsum +=d[i];

		double dbar = dsum / noPoints;
		double sum = 0.0;

		for (int i = 0; i < noPoints; i++) {
			sum += Math.pow(d[i] - dbar, 2.0);
		}

		return Math.sqrt(sum / (noPoints - 1));
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Private methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Manhattan distance
	 * @param ind1: First individual
	 * @param ind2: Second individual
	 * @return Manhattan distance between individuals
	 * */
	private double distance(double [] ind1, double [] ind2){
		double dist = 0.0;
		int numOfObj = ind1.length;

		for(int i=0; i<numOfObj; i++){
			dist += Math.abs(ind1[i]-ind2[i]);
		}
		return dist;
	}
}
