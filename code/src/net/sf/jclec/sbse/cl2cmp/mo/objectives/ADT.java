package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Average Data Types Metric. It computes
 * the mean number of different data types
 * among the components in the architecture.
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
public class ADT extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Serial ID */
	private static final long serialVersionUID = 13387014250086621L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public ADT() {
		super();
		setName("adt");
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// do nothing
	}

	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		double avg = 0.0;
		int numOfComponents = ind.getNumberOfComponents();
		int [] dataTypes = ind.getNumberOfDataTypes();
		for(int i=0; i<numOfComponents; i++){
			//System.out.println("i: " + i + " -> " + dataTypes[i]);
			avg += dataTypes[i];
		}
		avg = avg / (double)numOfComponents;
		//System.out.println("AvgDataType: " + avg);
		return new SimpleValueFitness(avg);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		int numOfClasses = getDataset().getColumns().size();
		double maxValue = (1.0/(double)getMinComponents()) * numOfClasses; 
		this.getInterval().setRight(maxValue);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}