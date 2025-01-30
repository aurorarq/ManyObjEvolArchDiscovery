package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Average number of interfaces
 * per component (ANIC). 
 * From "Complexity Metrics AS Predictors of
 * Maintainability and Integrability of
 * Software components" (2006)
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
public class ANIC extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 5748096610519215117L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public ANIC() {
		super();
		setName("anic");
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
		int numberOfComponents = ind.getNumberOfComponents();
		double avg = 0.0;
		for(int i=0; i<numberOfComponents; i++){
			avg += ind.getNumberOfProvided(i);
		}
		avg /= (double)numberOfComponents;
		//System.out.println("ANIC: " + avg);
		return new SimpleValueFitness(avg);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight((double)getMaxInterfaces()/(double)getMinComponents());
		this.getInterval().setClosure(Closure.OpenClosed);
	}
}