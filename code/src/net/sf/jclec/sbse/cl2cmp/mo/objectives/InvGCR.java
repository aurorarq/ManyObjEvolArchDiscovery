package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Inverse of GCR Metric. For
 * maximization problem formulation.
 * 
 * <p>History:
 * <ul>
 * 	<li>2.0: Adaptation to jclec-moeas (May 2014)
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 *  
 * @author Aurora Ramirez Quesada
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 2.0
 * @see GCR
 * */
public class InvGCR extends GCR {


	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8764903139279632079L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public InvGCR(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness compute(Cl2CmpMoIndividual ind) {
		IFitness res = super.compute(ind);
		double value = ((SimpleValueFitness)res).getValue();
		((SimpleValueFitness)res).setValue(-1.0*value);
		return res;
	}
	
	@Override
	public void computeBounds() {
		this.getInterval().setRight(-1.0);
		double numOfClasses = getDataset().getColumns().size();
		this.getInterval().setLeft(-1.0*(numOfClasses/getMinComponents()));
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
