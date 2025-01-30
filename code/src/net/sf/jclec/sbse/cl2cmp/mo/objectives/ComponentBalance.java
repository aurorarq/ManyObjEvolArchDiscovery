package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Component Balance metric. This metric
 * evaluates the balance between the number
 * of components and their sizes. 
 * From "Quantifying the analyzability
 * of software architectures" (2011)
 * 
 * <p>History:
 * <ul>
 *	<li>1.0: Creation (June 2014)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * @see Cl2CmpObjective
 * */
public class ComponentBalance extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 4413401102391954191L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public ComponentBalance() {
		super();
		setName("cb");
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// do nothing
	}

	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		double res;
		double sb, csu;

		// System breakdown value
		int opt = (int)(((float)getMaxComponents()+(float)getMinComponents())/2.0);
		int n = ind.getNumberOfComponents();
		int min = getMinComponents();
		int max = getMaxComponents();
		if(min!=max){
			if(n<opt){
				sb = ((float)(n-min))/((float)(opt-min));
			}
			else{
				sb = 1-((float)(n-opt)/(float)(max-opt));
			}
		}
		else
			sb = 1;

		// Component size uniformity
		double [] sizes = new double[n];
		for(int i=0; i<n; i++){
			sizes[i] = ind.getNumberOfClasses(i);
		}
		csu = 1.0 - giniCoefficient(sizes);

		// Result
		res = sb*csu;
		return new SimpleValueFitness(res);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(1.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}

	/**
	 * Calculate the gini coefficient
	 * for a given array of values
	 * @param sizes The array with the size of each component
	 * @return Gini coefficient
	 *  */
	protected double giniCoefficient(double [] sizes){

		// Order the sizes
		double aux;
		for(int i=0; i<sizes.length-1; i++)
			for(int j=i+1; j<sizes.length; j++)
				if(sizes[i] > sizes[j]){
					aux = sizes[j];
					sizes[j] = sizes[i];
					sizes[i] = aux;
				}

		// Accumulate
		double acc [] = new double[sizes.length];
		for(int i=0; i<acc.length; i++){
			acc[i]=sizes[i];
		}
		for(int i=1; i<acc.length;i++){
			acc[i] += acc[i-1];
		}
		
		// Gini coefficient
		double area_ab = (acc[acc.length-1]*acc.length)/2.0;
		double area_b = acc[0]/2.0;	
		for(int i=1; i<acc.length; i++){
			area_b += (acc[i-1]+acc[i])/2.0;
		}
		double gini = (area_ab-area_b)/area_ab;
		return gini;
	}
}