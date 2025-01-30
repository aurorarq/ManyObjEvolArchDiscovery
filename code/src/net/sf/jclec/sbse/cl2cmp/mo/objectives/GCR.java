package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Groups/components ratio (GR) metric for the
 * 'Classes to Components' (CL2CMP) problem.
 * <p>History:
 * <ul>
 * 	<li>2.0: Now extending Cl2CmpObjective (May 2014)
 * 	<li>1.1: Adaptation to EMO algorithms design (December 2013)
 * 	<li>1.0: Creation (September 2013)
 * </ul>
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 2.0
 * */
public class GCR extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8353947445604716289L;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public GCR(){
		super();
		setName("gcr");
	}
	
	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// Do nothing, needed metrics 
		// are properties of the individual
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		double numberOfComponents = (double)ind.getNumberOfComponents();
		double numberOfGroups = 0.0;

		// Total number of groups
		for(int i=0; i<numberOfComponents; i++){
			numberOfGroups += ind.getNumberOfGroups(i);
		}	
		return new SimpleValueFitness(numberOfGroups/numberOfComponents);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(1.0);
		double numOfClasses = getDataset().getColumns().size();
		this.getInterval().setRight(numOfClasses/getMinComponents());
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
