package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Abstractness Metric. Number of components 
 * exceeding a critical size (threshold). 
 * From "Reconstruction of Software Component 
 * Architectures and Behaviour Models using 
 * Static and Dynamic Analysis" (2010)
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (June 2014)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * @see Cl2CmpObjective
 * */
public class Abstractness extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8644992864238274471L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public Abstractness() {
		super();
		setName("abs");
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// Do nothing
	}

	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		// Abstractness of each component
		int numOfComponents = ind.getNumberOfComponents();
		double avg = 0.0;
		int numOfAbstractClasses [] = ind.getNumberOfAbstractClasses();
		for(int i=0; i<numOfComponents; i++){
			avg += (double)numOfAbstractClasses[i]/(double)ind.getNumberOfClasses(i);
		}	
		// Mean abstractness in the architecture
		avg /= numOfComponents;
		return new SimpleValueFitness(avg);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(1.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
