package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import org.apache.commons.configuration.Configuration;

import es.uco.kdis.datapro.dataset.Dataset;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.fitness.Objective;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;

/**
 * An abstract objective for the evaluation of
 * a desirable design characteristic in the
 * 'Classes to Components' (CL2CMP) problem.
 * 
 * <p>History:
 * <ul>
 * 	<li>2.0: Now extending Objective
 * 	<li>1.0: Creation
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * 
 * @version 2.0
 * @see Objective
 * */
public abstract class Cl2CmpObjective extends Objective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -5228349782111999472L;

	/** Dataset that contains the problem information (analysis model) */
	protected Dataset dataset;

	/** Objective name */
	protected String name;

	/** Maximum number of components */
	private int maxComponents;
	
	/** Minimum number of components */
	private int minComponents;
	
	/** Maximum number of interfaces */
	private int maxInterfaces;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public Cl2CmpObjective(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the dataset
	 * @return The dataset
	 * */
	public Dataset getDataset(){
		return this.dataset;
	}

	/**
	 * Set the dataset
	 * @param dataset The dataset
	 * */
	public void setDataset(Dataset dataset){
		this.dataset = dataset;
	}
	
	/**
	 * Get the objective name
	 * @return The objective name
	 * */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Set the objective name
	 * @param name The new name
	 * */
	protected void setName(String name){
		this.name = name;
	}
	
	/**
	 * Get the maximum number
	 * of components
	 * @return maximum number
	 * of components configured
	 * */
	public int getMaxComponents(){
		return this.maxComponents;
	}
	
	/**
	 * Set the maximum number
	 * of components
	 * @param maxComponents The
	 * maximum number of components
	 * */
	public void setMaxComponents(int maxComponents){
		this.maxComponents = maxComponents;
	}
	
	/**
	 * Get the minimum number
	 * of components
	 * @return minimum number
	 * of components configured
	 * */
	public int getMinComponents(){
		return this.minComponents;
	}
	
	/**
	 * Set the minimum number
	 * of components
	 * @param minComponents The
	 * maximum number of components
	 * */
	public void setMinComponents(int minComponents){
		this.minComponents = minComponents;
	}
	
	/**
	 * Get the maximum number
	 * of candidate interfaces
	 * @return maximum number
	 * of interfaces extracted
	 * from the model
	 * */
	public int getMaxInterfaces(){
		return this.maxInterfaces;
	}
	
	/**
	 * Set the maximum number
	 * of candidate interfaces
	 * @param maxInterfaces The
	 * maximum number of components
	 * */
	public void setMaxInterfaces(int maxInterfaces){
		this.maxInterfaces = maxInterfaces;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	@Override
	public void configure(Configuration settings) {
		super.configure(settings);
	}
	
	/**
	 * {@inheritDoc}
	 * The computation of the objective will be
	 * performed in two steps:
	 * <p>Prepare the computation (get auxiliary measures)
	 * <p>Compute the objective value
	 * */
	@Override
	public IFitness evaluate(IIndividual ind){
		// Prepare and compute the objective value
		prepare((Cl2CmpMoIndividual)ind);
		return compute((Cl2CmpMoIndividual)ind);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Prepare the computation. Required characteristics
	 * are taken from the individual.
	 * @param ind The individual
	 * */
	protected abstract void prepare(Cl2CmpMoIndividual ind);

	/**
	 * Compute the objective value. <code>prepare()</code> 
	 * should be executed before to prepare computation.
	 * @see prepare(IIndividual ind)
	 * @return Result of the metric
	 * */
	protected abstract IFitness compute(Cl2CmpMoIndividual ind);
	
	/**
	 * Compute the bounds of the objective
	 * function. It allows to properly
	 * initialize the objectives when
	 * they are dependent of the problem instance
	 * or the algorithm configuration.
	 * */
	public abstract void computeBounds();
}
