package net.sf.jclec.sbse.cl2cmp.mo.algorithms;

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.strategies.MOEAD;
import net.sf.jclec.mo.strategies.constrained.ConstrainedMOEAD;
import net.sf.jclec.util.random.IRandGen;

/**
 * This class extend the ConstrainedMOEAD
 * evolutionary strategy to adapt the
 * mating selection procedure to the CL2CMP
 * problem.
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (May 2014)</li>
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @see ConstrainedMOEAD
 * @see MOEAD
 * @version 1.0
 * */
public class Cl2CmpMOEAD extends ConstrainedMOEAD {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8775546248449998226L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public Cl2CmpMOEAD(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>As only mutation is performed in this problem,
	 * only one neighbor for each individual is selected
	 * to act as a parent.
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> inhabitants, List<IIndividual> archive) {
		int index;
		int problemID;
		List<IIndividual> parents = new ArrayList<IIndividual>();
		int size = inhabitants.size();
		IRandGen randgen = getContext().createRandGen();

		for(int i=0; i<size; i++){
			// Select one random neighbor for each subproblem (individual in the population)
			index = randgen.choose(0, getNeighborhoodSize());
			problemID = getProblemNeighbors().get(i).get(index).getId();
			parents.add(inhabitants.get(problemID));
		}
		return parents;
	}
}
