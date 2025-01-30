package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Inverse of CritSize Metric. For
 * minimization problem formulation.
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (June 2014)
 * </ul>
 * 
 * @see Abstractness
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class InvAbstractness extends Abstractness {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 4948026852858430685L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public InvAbstractness() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		IFitness res = super.compute(ind);
		double value = ((SimpleValueFitness)res).getValue();
		((SimpleValueFitness)res).setValue(-1.0*value);
		return res;
	}
	
	@Override
	public void computeBounds() {
		this.getInterval().setLeft(-1.0);
		this.getInterval().setRight(0.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}