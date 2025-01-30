package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

public class InvADT extends ADT {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2789168157951079877L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public InvADT() {
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
		int numOfClasses = getDataset().getColumns().size();
		double maxValue = (1/getMinComponents()) * numOfClasses; 
		this.getInterval().setLeft(-1.0*maxValue);
		this.getInterval().setRight(0.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
