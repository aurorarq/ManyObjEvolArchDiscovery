package net.sf.jclec.sbse.cl2cmp.mo.exp;

import net.sf.jclec.JCLEC;

/**
 * Hypervolume indicator. Adapted from the 
 * implementation of jMetal 
 * (http://jmetal.sourceforge.net/). It
 * allows maximization problems with
 * n objectives (n>=2).
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */

public class Hypervolume implements JCLEC{

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1326235296743142527L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public Hypervolume(){
		// Do nothing
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------- Public methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Calculate the hypervolume metric for a given front
	 * @param front: the pareto front
	 * @return Hypervolume value for the pareto front
	 * */
	public double calculateHypervolume(double [][] front, int  noPoints,int  noObjectives){
		int n;
		double volume, distance;

		volume = 0;
		distance = 0;
		n = noPoints;
		while (n > 0) {
			int noNondominatedPoints;
			double tempVolume, tempDistance;

			noNondominatedPoints = filterNondominatedSet(front, n, noObjectives - 1);
			tempVolume = 0;
			if (noObjectives < 3) {
				if (noNondominatedPoints < 1)  
					System.err.println("run-time error");

				tempVolume = front[0][0];
			} else
				tempVolume = calculateHypervolume(front,
						noNondominatedPoints,
						noObjectives - 1);

			tempDistance = surfaceUnchangedTo(front, n, noObjectives - 1);
			volume += tempVolume * (tempDistance - distance);
			distance = tempDistance;
			n = reduceNondominatedSet(front, n, noObjectives - 1, distance);
		}
		return volume;
	} 

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Private methods
	//////////////////////////////////////////////////////////////////

	/**
	 * All nondominated points regarding the first 'noObjectives' 
	 * dimensions are collected; the points referenced by 'front[0..noPoints-1]' are
	 * considered; 'front' is resorted, such that 'front[0..n-1]' contains
	 * the nondominated points; n is returned.
	 * */
	private int  filterNondominatedSet(double [][] front, int  noPoints, int  noObjectives){
		int  i, j;
		int  n;

		n = noPoints;
		i = 0;
		while (i < n) {
			j = i + 1;
			while (j < n) {
				if (dominates(front[i], front[j], noObjectives)) {
					/* remove point 'j' */
					n--;
					swap(front, j, n);
				} else if (dominates(front[j], front[i], noObjectives)) {
					/* remove point 'i'; ensure that the point copied to index 'i'
		   			is considered in the next outer loop (thus, decrement i) */
					n--;
					swap(front, i, n);
					i--;
					break;
				} else
					j++;
			}
			i++;
		}
		return n;
	}


	/** 
	 * Returns true if 'point1' dominates 'points2' with respect to the 
	 * to the first 'noObjectives' objectives 
	 * */
	private boolean  dominates(double  point1[], double  point2[], int  noObjectives) {
		int  i;
		int  betterInAnyObjective;

		betterInAnyObjective = 0;
		for (i = 0; i < noObjectives && point1[i] >= point2[i]; i++)
			if (point1[i] > point2[i]) 
				betterInAnyObjective = 1;
		
		return ((i >= noObjectives) && (betterInAnyObjective>0));
	}

	/**
	 * Swap method
	 * */
	private void  swap(double [][] front, int  i, int  j){
		double  [] temp;

		temp = front[i];
		front[i] = front[j];
		front[j] = temp;
	}

	/**
	 * Calculate next value regarding dimension 'objective'; consider
	 * points referenced in 'front[0..noPoints-1]' */
	private double  surfaceUnchangedTo(double [][] front, int  noPoints, int  objective) {
		int     i;
		double  minValue, value;

		if (noPoints < 1)  
			System.err.println("run-time error");

		minValue = front[0][objective];
		for (i = 1; i < noPoints; i++) {
			value = front[i][objective];
			if (value < minValue)
				minValue = value;
		}
		return minValue;
	}

	/**
	 * Remove all points which have a value <= 'threshold' regarding the
	 * dimension 'objective'; the points referenced by
	 * 'front[0..noPoints-1]' are considered; 'front' is resorted, such that
	 * 'front[0..n-1]' contains the remaining points; 'n' is returned 
	 * */
	private int reduceNondominatedSet(double [][] front, int  noPoints, int  objective,
			double  threshold){
		int  n;
		int  i;

		n = noPoints;
		for (i = 0; i < n; i++)
			if (front[i][objective] <= threshold) {
				n--;
				swap(front, i, n);
			}

		return n;
	}
}