package net.sf.jclec.sbse.cl2cmp.mo.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractMutator;
import net.sf.jclec.mo.MOAlgorithm;
import net.sf.jclec.mo.comparators.MOIndividualsComparator;
import net.sf.jclec.mo.strategies.MOEAD;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoEvaluator;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoSpecies;

/**
 * Multi-Objective Evolutionary
 * Algorithm for Cl2Cmp problem.
 * 
 * It implements the general configuration,
 * initialization process and the generation 
 * of descendants by mutation.
 * 
 * @author Aurora Ramírez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * 
 * @version 1.0
 * History:
 * <ul>
 * 	<li>2.0: Now extending from MOAlgorithm (May 2014)
 * 	<li>1.0: Creation (November 2013)
 * </ul>
 * @see MOAlgorithm
 * */
public class Cl2CmpMoAlgorithm extends MOAlgorithm {

	/** Serial ID */
	private static final long serialVersionUID = 1091571505216439237L;

	/** Mutator */
	private AbstractMutator mutator;

	/** Frequency of solution sizes */
	private int [] frequency;

	/** Minimum number of components */
	private int minOfComp;

	/** Maximum number of components */
	private int maxOfComp;

	/** Number of invalids in the current generation */
	private int invalids;

	/** Initial date time */
	private long initTime;

	/** Number of non dominated solutions in the current population */
	private int numberOfNonDominated;

	/** Evolution progress */
	private int progress;

	//private List<IIndividual> previousEset;

	private List<IIndividual> nonDominated;

	//private ParetoComparator progressComparator;

	//////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Constructor
	//////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public Cl2CmpMoAlgorithm(){
		super();
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Get/Set methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Get mutator
	 * @return the configured mutator
	 * */
	public AbstractMutator getMutator() {
		return mutator;
	}

	/**
	 * Set mutator
	 * @param mutator: the new mutator
	 * */
	private void setMutator(AbstractMutator mutator) {
		this.mutator = mutator;
	}

	/**
	 * Get minimum number of components
	 * @return The minimum number of components
	 * */
	public int getMinNumberOfComponents() {
		return minOfComp;
	}

	/**
	 * Set minimum number of components
	 * @param minOfComp: the new minimum limit
	 * */
	private void setMinNumberOfComponents(int minOfComp) {
		this.minOfComp = minOfComp;
	}

	/**
	 * Get maximum number of components
	 * @return The maximum number of components
	 * */
	public int getMaxNumberOfComponents() {
		return maxOfComp;
	}

	/**
	 * Set maximum number of components
	 * @param maxOfComp: the new maximum limit
	 * */
	private void setMaxNumberOfComponents(int maxOfComp) {
		this.maxOfComp = maxOfComp;
	}

	/**
	 * Get number of invalid solutions
	 * in the current population
	 * @return Number of invalid solutions
	 * */
	public int getNumberOfInvalids() {
		return invalids;
	}

	/**
	 * Set number of invalid individuals
	 * @param invalids: the number of invalid individuals
	 * */
	private void setNumberOfInvalids(int invalids) {
		this.invalids = invalids;
	}

	/**
	 * Get number of non dominated solutions
	 * in the current population
	 * @return Number of non dominated solutions
	 * */
	public int getNumberOfNonDominated() {
		return numberOfNonDominated;
	}

	/**
	 * Set number of non dominated individuals
	 * @param nonDominated The number of non dominated individuals
	 * */
	private void setNumberOfNonDominated(int nonDominated) {
		this.numberOfNonDominated = nonDominated;
	}

	/**
	 * Get number of non dominated solutions
	 * in the current population
	 * @return Number of non dominated solutions
	 * */
	public int getProgress() {
		return progress;
	}

	/**
	 * Set number of non dominated individuals
	 * @param numberOfNonDominated The number of non dominated individuals
	 * */
	private void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * Get initial time
	 * @return The initial time
	 * */
	public long getInitTime() {
		return initTime;
	}

	/**
	 * Set initial time
	 * @param initTime: the initial time
	 * */
	private void setInitTime(long initTime) {
		this.initTime = initTime;
	}

	/**
	 * Get time since the beginning of the algorithm
	 * @return the executed time
	 * */
	public long getTime(){
		return System.currentTimeMillis()-getInitTime();
	}

	/**
	 * Get the frequency of each type
	 * of solution (number of components) in
	 * the population
	 * @return Array with the size frequency
	 * */
	public int[] getComponentFrequency() {
		return frequency;
	}

	/**
	 * Get the frequency of each type
	 * of solution (number of components) in
	 * the population
	 * @param frequency: Array with the size frequency
	 * */
	private void setComponentFrequency(int [] frequency) {
		this.frequency = frequency;
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------- Protected override methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Do initialization
	 * */
	@Override
	protected void doInit(){

		// If strategy is MOEA/D, population size should be updated
		if(this.strategy instanceof MOEAD){
			int size = ((MOEAD)this.strategy).getNumberOfVectors();
			super.setPopulationSize(size);
			this.strategy.getContext().setPopulationSize(size);
		}
		
		// Set init time
		setInitTime(System.currentTimeMillis());

		// Create individuals
		this.bset = this.provider.provide(this.populationSize);

		// Evaluate individuals
		this.evaluator.evaluate(this.bset);
		
		// Create an empty external population
		this.eset = new ArrayList<IIndividual>();

		// Initialize strategy
		this.strategy.initialize(this.bset, this.eset);

		this.nonDominated = getNonDominatedSolutions();

		// Verbose
		updateStatistics();

		// Do Control
		doControl();
	}

	/**
	 * Do generation. Only mutation is performed.
	 * */
	@Override
	protected void doGeneration() {
		// Mutate individuals and evaluate them
		this.cset = this.mutator.mutate(this.pset);
		this.evaluator.evaluate(this.cset);

		// Copy the current eset before do the replacement
		//this.previousEset = this.eset;

		// Copy the current non dominated solutions
		this.nonDominated = getNonDominatedSolutions();
	}

	/**
	 * Do update
	 * */
	@Override
	protected void doUpdate() {
		// Verbose
		updateStatistics();

		// Update current population
		super.doUpdate();
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------ Configuration methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Configure
	 * @param settings: the configuration object
	 * */
	@Override
	public void configure(Configuration settings){
		super.configure(settings);

		// Set algorithm parameters
		int numOfClasses = settings.getInt("model.num-of-classes");
		int min = settings.getInt("min-of-components", 2);
		int max = settings.getInt("max-of-components", numOfClasses);
		this.frequency = new int [max-min+1];

		// Check the compatibility between classes and components
		if(min<2 || max>numOfClasses){
			throw new IllegalArgumentException("Illegal limits for number of components");
		}

		setMinNumberOfComponents(min);
		setMaxNumberOfComponents(max);

		// Set species parameters
		String datasetRel = settings.getString("model.relations");
		String datasetCls = settings.getString("model.classes");
		if(datasetRel!=null && datasetCls!=null){
			// Set dataset in species and evaluator
			try{
				((Cl2CmpMoSpecies)this.getSpecies()).setDataset(datasetRel, numOfClasses);
				((Cl2CmpMoEvaluator)this.getEvaluator()).configureDatasets(datasetRel, datasetCls, numOfClasses);
			}
			catch (IndexOutOfBoundsException|IOException|
					NotAddedValueException|IllegalFormatSpecificationException e) {
				System.err.println("Malformed dataset: " + e.getMessage());
				System.exit(1);
			}
		}
		else
			System.err.println("Two datasets of the problem instance must be specified");

		// Configure problem parameters in species and evaluator
		((Cl2CmpMoSpecies)this.getSpecies()).setConstraints(min, max);

		// If all navigable relationships is considered as candidate interface...
		int maxInterfaces = (((Cl2CmpMoSpecies)this.getSpecies()).getGenotypeSchema().getTerminals().length 
				- ((Cl2CmpMoSpecies)this.getSpecies()).getGenotypeSchema().getNumOfClasses())/2;
		//System.out.println("maxInt: " + maxInterfaces);
		((Cl2CmpMoEvaluator)this.getEvaluator()).setProblemCharacteristics(min, max, maxInterfaces);

		// Configure mutator
		try {
			setMutatorSettings(settings.subset("base-mutator"));
		} catch (InstantiationException| IllegalAccessException | ClassNotFoundException e){
			System.err.println("Configuration error: " + e.getMessage());
			System.exit(1);
		}

		// Configure comparator for eProgress
		//createProgressComparator();
	}

	/**
	 * Set mutation configuration
	 * @param settings: the configuration object
	 * @throws InstantiationException
	 * @throws IllegalAccessExceptionl
	 * @throws ClassNotFoundException
	 * */
	@SuppressWarnings("unchecked")
	private void setMutatorSettings(Configuration settings) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException{

		// Mutation class instantiation
		String classname = settings.getString("[@type]");
		Class<? extends AbstractMutator> mutatorClass;

		// Create
		mutatorClass = (Class<? extends AbstractMutator>) Class.forName(classname);
		AbstractMutator mutator = mutatorClass.newInstance();

		// Contextualize
		mutator.contextualize(this);

		// Configure
		if (mutator instanceof IConfigure)
			((IConfigure) mutator).configure(settings);

		// Set in algorithm
		setMutator(mutator);
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Private methods
	//////////////////////////////////////////////////////////////////

	private void updateStatistics(){

		// Individuals frequency (types of architectural solutions)
		countFrequency();

		// Number of invalids
		countInvalids();

		// Number of non dominated solutions
		int n = this.getNonDominatedSolutions().size();
		setNumberOfNonDominated(n);

		// Evolution progress
		if(getGeneration()>0)
			evolutionProgress();
		else
			setProgress(0);
	}

	/**
	 * Count frequency of each size of solution
	 * and save it in <code>frequency</code>
	 * */
	private void countFrequency(){
		int index;
		int min = getMinNumberOfComponents();
		int size = getMaxNumberOfComponents()-min+1;
		int frequency [] = new int[size];

		for(int i=0; i<frequency.length; i++)
			frequency[i]=0;
		for(IIndividual ind: this.bset){
			index = ((Cl2CmpMoIndividual)ind).getNumberOfComponents();
			frequency[index-min]++;
		}
		setComponentFrequency(frequency);
	}

	/**
	 * Count number of invalid solutions
	 * and save it in <code>invalids</code>
	 * */
	private void countInvalids(){
		int count = 0;
		for(IIndividual ind: this.bset){
			if(((Cl2CmpMoIndividual)ind).isInvalid())
				count++;
		}
		setNumberOfInvalids(count);
	}

	/**
	 * 
	 * */
	/*private void createProgressComparator(){
		List<Objective> objectives = ((MOEvaluator)getEvaluator()).getObjectives();
		Comparator<IFitness> componentComparators [] = null;
		int nObj = objectives.size();
		componentComparators = new eProgressFitnessComparator[nObj];
		Objective obj;
		double epsilon;
		for(int i=0; i<nObj; i++){
			obj = objectives.get(i);
			//epsilon = Math.abs(obj.getInterval().getRight()-obj.getInterval().getLeft())/10;
			componentComparators[i] = new eProgressFitnessComparator();
			((eProgressFitnessComparator)componentComparators[i]).setInverse(!obj.isMaximized());
			((eProgressFitnessComparator)componentComparators[i]).setEpsilon(0.0);
		}
		this.progressComparator = new ParetoComparator(componentComparators);
	}*/


	/**
	 * 
	 * */
	private void evolutionProgress(){

		// Current population + archive

		List<IIndividual> current = new ArrayList<IIndividual>(this.nonDominated);
		/*if(previousEset!=null){
			for(IIndividual ind: previousEset){
				if(!current.contains(ind)){
					current.add(ind);
				}
			}
		}*/

		// New individuals to conform the next population + updated archive
		List<IIndividual> next = new ArrayList<IIndividual>();
		for(IIndividual ind: this.rset){
			if(!current.contains(ind)){
				next.add(ind);
			}
		}
		if(eset!=null){
			for(IIndividual ind: this.eset){
				if(!current.contains(ind) && !next.contains(ind)){
					next.add(ind);
				}
			}
		}

		int size1 = next.size();
		int size2 = current.size();
		IIndividual indCurrent, indNext;
		int progress = 0;
		boolean dominates;
		// Check dominance between both generations
		for(int i=0; i<size1; i++){
			indNext = next.get(i);
			dominates = false;
			for(int j=0; !dominates && j<size2; j++){
				indCurrent = current.get(j);
				switch(this.strategy.getIndividualsComparator().compare(indNext, indCurrent)){
				// the new individual dominates at least one individual in the previous population
				case 1: dominates=true;
				progress++;
				break;
				case -1: break;
				case 0: break;
				}
			}
		}
		// update progress
		setProgress(progress);
	}

	/**
	 * Get final individuals, i.e. the solutions
	 * to be presented to the user. It removes
	 * equivalent individuals from the final
	 * set of solution of each specific algorithm
	 * @return Final set of individuals
	 * */
	public List<IIndividual> getFinalIndividuals(){

		// Get all the solutions found by the algorithm
		List<IIndividual> all = new ArrayList<IIndividual>();
		// If the algorithm uses external population, it is the set of final solutions
		if(this.eset!=null)
			for(IIndividual ind: this.eset)
				all.add(ind.copy());
		// Otherwise, get current population
		else
			for(IIndividual ind: this.bset)
				all.add(ind.copy());

		List<IIndividual> finalSolutions = new ArrayList<IIndividual>();

		// Remove equivalent solutions, invalid individuals and dominated solutions
		int j;
		MOIndividualsComparator indComparator = this.strategy.getIndividualsComparator();
		for(int i=0; i<all.size(); i++){
			Cl2CmpMoIndividual ind1 = (Cl2CmpMoIndividual) all.get(i);
			j=i+1;
			while(j<all.size()){
				Cl2CmpMoIndividual ind2 = (Cl2CmpMoIndividual) all.get(j);
				if(ind1.isEquivalent(ind2)
						|| indComparator.compare(ind1, ind2)==1){
					all.remove(j);
				}
				else
					j++;
			}
			if(!ind1.isInvalid())
				finalSolutions.add(ind1);
		}
		// Return final solutions
		return finalSolutions;
	}

	/*private void print(IIndividual ind){
		MOFitness f = (MOFitness)ind.getFitness();
		int n = f.getComponents().length;
		//System.out.println(ind);
		for(int i=0; i<n; i++){
			try {
				System.out.print(f.getComponentValue(i) + " ");
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
	}*/
}
