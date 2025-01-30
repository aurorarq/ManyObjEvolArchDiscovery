package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.util.range.Closure;

/**
 * Critical Link Metric. Number of components 
 * exceeding a critical number of links, 
 * i.e. the threshold). Links are the
 * provided interfaces. 
 * From "Some theoretical considerations for 
 * a suite of metrics for the integration 
 * of software components" (2007)
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
public class CritLink extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -791471130470536792L;

	/** Critical threshold */
	protected int threshold;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public CritLink() {
		super();
		setName("cl");
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Specific parameter for CritSize is:
	 * <ul>
	 * 	<li>link-threshold (<code>int</code>):
	 * 	Maximum number of links.
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {

		super.configure(settings);

		// Configure link threshold
		Object property = settings.getProperty("objective("+getIndex()+").link-threshold");
		if(property == null){
			throw new IllegalArgumentException("A critical link threshold must be specified");
		}
		else{
			try{
				this.threshold = Integer.parseInt(property.toString());
				if(this.threshold < 0){
					throw new IllegalArgumentException("The critical size threshold must be greater than 0");
				}
			}catch(NumberFormatException e){
				throw new IllegalArgumentException("The critical link threshold must be an integer value");
			}
		}
	}

	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// Do nothing
	}

	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		double numOfCriticalComponents = 0.0;
		int numOfComponents = ind.getNumberOfComponents();
		for(int i=0; i<numOfComponents; i++){
			if(ind.getNumberOfProvided(i)>this.threshold){
				numOfCriticalComponents++;
			}
		}
		return new SimpleValueFitness(numOfCriticalComponents);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(getMaxComponents());
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
