package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Critical Size Metric. Number of components 
 * exceeding a critical size (threshold). 
 * From "Some theoretical considerations for 
 * a suite of metrics for the integration 
 * of software components" (2007)
 * 
 * <p>History:
 * <ul>
 * 	<li>2.0: Now extending Cl2CmpObjective, normalization (May 2014)
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 2.0
 * @see Cl2CmpObjective
 * */
public class CritSize extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7933325759620299784L;

	/** Threshold of critically as a percentage of the number
	 * of classes in the problem */
	protected double threshold;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public CritSize(){
		super();
		setName("cs");
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Specific parameter for CritSize is:
	 * <ul>
	 * 	<li>size-threshold (<code>double</code>):
	 * 	Maximum percentage of classes contained
	 *  inside of a component in relation to the 
	 *  total number of classes in the initial 
	 *  analysis model.
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {

		super.configure(settings);

		// Configure size threshold
		Object property = settings.getProperty("objective("+getIndex()+").size-threshold");
		if(property == null){
			throw new IllegalArgumentException("A critical size threshold must be specified");
		}
		else{
			try{
				this.threshold = Double.parseDouble(property.toString());
				if(this.threshold < 0.0 || this.threshold > 1.0){
					throw new IllegalArgumentException("The critical size threshold varies between 0.0 and 1.0");
				}
			}catch(NumberFormatException e){
				throw new IllegalArgumentException("The critical size threshold must be a double value");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void prepare(Cl2CmpMoIndividual ind) {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness compute(Cl2CmpMoIndividual ind) {
		double numOfCriticalComponents = 0.0;
		int maxNumOfClasses = (int)Math.round(getDataset().getColumns().size()*this.threshold);
		int numOfComponents = ind.getNumberOfComponents();
		for(int i=0; i<numOfComponents; i++){
			if(ind.getNumberOfClasses(i)>maxNumOfClasses){
				numOfCriticalComponents++;
			}
		}
		return new SimpleValueFitness(numOfCriticalComponents/(double)numOfComponents);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(getMaxComponents());
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
