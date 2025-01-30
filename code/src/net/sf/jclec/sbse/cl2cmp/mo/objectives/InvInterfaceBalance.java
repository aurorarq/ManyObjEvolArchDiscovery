package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Inverse of InterfaceBalance Metric. For
 * minimization problems.
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (June 2014)
 * </ul>
 * 
 * @see ComponentBalance
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class InvInterfaceBalance extends InterfaceBalance {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3264523811760784166L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public InvInterfaceBalance() {
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
		this.getInterval().setLeft(-1.0);
		this.getInterval().setRight(0.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}