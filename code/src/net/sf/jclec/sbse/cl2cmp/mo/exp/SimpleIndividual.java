package net.sf.jclec.sbse.cl2cmp.mo.exp;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;

public class SimpleIndividual implements IIndividual {

	private static final long serialVersionUID = -1827321387638026933L;
	protected IFitness fitness;

	public SimpleIndividual() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IFitness getFitness() {
		// TODO Auto-generated method stub
		return this.fitness;
	}

	@Override
	public void setFitness(IFitness fitness) {
		this.fitness = fitness;

	}

	@Override
	public double distance(IIndividual other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IIndividual copy() {
		// Create new individual
		SimpleIndividual ind = new SimpleIndividual();

		// Copy fitness and metrics
		if(this.fitness != null){
			ind.setFitness(this.getFitness());
		}
		return ind;
	}
}