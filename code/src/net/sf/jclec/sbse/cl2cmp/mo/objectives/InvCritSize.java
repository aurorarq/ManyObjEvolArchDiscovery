package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Inverse of CritSize Metric. For
 * maximization problem formulation.
 * 
 * <p>History:
 * <ul>
 * 	<li>2.0: Adaptation to jclec-moea (May 2014)
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * 
 * @see CritSize
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 2.0
 * */
public class InvCritSize extends CritSize {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7660687462629547217L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public InvCritSize(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	@Override
	public IFitness compute(Cl2CmpMoIndividual ind) {
		IFitness res = super.compute(ind);
		double value = ((SimpleValueFitness)res).getValue();
		((SimpleValueFitness)res).setValue(-1.0*value);
		return res;
	}
	
	@Override
	public void computeBounds() {
		this.getInterval().setLeft(-1.0*getMaxComponents());
		this.getInterval().setRight(0.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
