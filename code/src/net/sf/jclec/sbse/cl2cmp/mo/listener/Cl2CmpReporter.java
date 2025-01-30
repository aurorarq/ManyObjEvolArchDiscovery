package net.sf.jclec.sbse.cl2cmp.mo.listener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.Column.ColumnAbstraction;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.MOAlgorithm;
import net.sf.jclec.mo.fitness.Objective;
import net.sf.jclec.mo.fitness.MOFitness;
import net.sf.jclec.mo.listener.MOPopulationReporter;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoEvaluator;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.sbse.cl2cmp.mo.algorithms.Cl2CmpMoAlgorithm;
import net.sf.jclec.sbse.cl2cmp.mo.objectives.Cl2CmpObjective;

/**
 * This class implements the general functionality
 * of the listeners for the CL2CMP problem.
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation
 * </ul>
 * @version 1.0
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * */
public abstract class Cl2CmpReporter extends MOPopulationReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2867629454181654864L;

	/** Report dataset */
	protected Dataset resultsDataset;

	/** The report already exits (for multiple executions) */
	protected boolean isFirstExecution;

	/** Attributes to be saved in the dataset (general information) */
	protected String [] colsNames;

	/** Attributes to be saved in the dataset (metrics used in the algorithm) */
	protected String [] objNames;

	/** Number of executions */
	protected int numberOfExectutions;
	
	/** Number of executions */
	protected int currentExecution;

	/** Directory name */
	protected String dirname;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public Cl2CmpReporter(){
		super();
		this.isFirstExecution = false;
		dirname = "res";
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for Cl2CmpReporter are:
	 * <ul>
	 * 	<li>number-of-executions (<double>int</double>):
	 * 	Number of executions to be reported
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);
		this.numberOfExectutions = settings.getInt("number-of-executions",1);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {
		
		Cl2CmpMoAlgorithm algorithm = (Cl2CmpMoAlgorithm)event.getAlgorithm();
		createReportTitle(algorithm);
		
		// Create report file
		this.reportFile = new File(dirname + "/" + this.reportTitle + ".xlsx");
		if(!this.reportFile.exists()){
			if(this.reportFile.getParentFile()!=null)
				this.reportFile.getParentFile().mkdirs();
			this.isFirstExecution=true;
			this.currentExecution=1;
		}
		else
			this.isFirstExecution=false;

		// First execution
		if(this.isFirstExecution){

			// Create datasets if it is the first execution
			this.resultsDataset = new ExcelDataset();
			this.resultsDataset.setName(this.reportTitle);

			// Add a column with the name of the general metrics to be saved
			this.resultsDataset.addColumn(new NominalColumn("Execution"));
			for(int i=0; i<this.colsNames.length; i++)
				this.resultsDataset.addColumn(new NumericalColumn(this.colsNames[i]));

			// Get the name of the fitness metrics used
			// Add a column with the name of the fitness metrics to be saved
			for(int i=0; i<this.objNames.length; i++){
				this.resultsDataset.addColumn(new NumericalColumn(this.objNames[i]));
			}
		}

		// Other executions, open the dataset
		else{
			this.resultsDataset = new ExcelDataset(dirname + "/" + this.reportTitle + ".xlsx");
			try {
				String format = "s";	// The first column is nominal
				// Concatenate 'f' for numerical columns
				int numOfCols = this.colsNames.length + this.objNames.length;
				for(int i=0; i<numOfCols; i++)
					format = format.concat("f");
				this.resultsDataset.setNullValue("?");
				((ExcelDataset)this.resultsDataset).readDataset("nv", format);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void iterationCompleted(AlgorithmEvent event) {
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmFinished(AlgorithmEvent event) {
		// Do last generation report
		doReport((MOAlgorithm) event.getAlgorithm());

		// Save dataset
		try {
			// If it is the last execution, compute mean and standard deviation
			int size = this.resultsDataset.getColumn(0).getSize();
			if(this.numberOfExectutions>1 && size==this.numberOfExectutions){
				finalStatistics(this.resultsDataset);
			}
			((ExcelDataset)this.resultsDataset).writeDataset(dirname + "/" + this.reportTitle + ".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmTerminated(AlgorithmEvent e) {
		// do nothing
	}

	/**
	 * Do an iteration report
	 * @param algorithm The algorithm
	 * */
	protected abstract void doReport(MOAlgorithm algorithm);

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Private methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Get the average value of a specified objective
	 * in the individual set
	 * @param inhabitans The individuals set
	 * @param index The index of the objective
	 * @return mean value of metric in individuals
	 * */
	protected double mean(List<IIndividual> inhabitans, int index){
		double avg = 0.0;
		int size = inhabitans.size();
		int n = 0;
		double value;
		if(size>0){
			for(int i=0; i<size; i++){
				try {
					value = ((MOFitness)inhabitans.get(i).getFitness()).getComponentValue(index);
					if(value<0)
						value=value*(-1);
					avg += value;
					n++;
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			avg/=n;
			return avg;
		}
		else
			return 0;
	}
	
	/**
	 * Return the mean of components in the population
	 * @param inhabitants The list of individuals
	 * @return The mean value
	 * */
	protected double averageComponents(List<IIndividual> inhabitants){
		double avg = 0.0;
		int size = inhabitants.size();
		if(size>0){
			for(int i=0; i<size; i++)
				avg += ((Cl2CmpMoIndividual)(inhabitants.get(i))).getNumberOfComponents();
			avg /= size;
		}
		return avg;
	}

	/**
	 * Return the mean of connectors in the population
	 * @param inhabitants The list of individuals
	 * @return The mean value
	 * */
	protected double averageConnectors(List<IIndividual> inhabitants){
		double avg = 0.0;
		int size = inhabitants.size();
		if(size>0){
			for(int i=0; i<size; i++)
				avg += ((Cl2CmpMoIndividual)(inhabitants.get(i))).getNumberOfConnectors();
			avg /= size;
		}
		return avg;
	}

	/**
	 * Compute final statistics (mean
	 * and standard deviation) over all
	 * executions for a given dataset
	 * @param dataset The dataset
	 * */
	protected void finalStatistics(Dataset dataset){
		// Compute mean and standard deviation for each column
		List<ColumnAbstraction> cols = dataset.getColumns();
		int n = cols.size();
		NumericalColumn col;
		double avg, sd;
		cols.get(0).addValue("MEAN");
		cols.get(0).addValue("SD");
		for(int i=1; i<n; i++){
			col = (NumericalColumn) cols.get(i);
			avg = col.mean();
			sd = col.standardDeviation();
			col.addValue(avg);
			col.addValue(sd);
		}
	}
	
	/**
	 * Create the report title with the parameters
	 * of the algorithm
	 * @param algorithm executed algorithm
	 * */
	protected void createReportTitle(Cl2CmpMoAlgorithm algorithm){
		
		// Names of the objectives
		String objectiveNames = "";
		List<Objective> objectives = ((Cl2CmpMoEvaluator)algorithm.getEvaluator()).getObjectives();
		this.objNames = new String[objectives.size()];
		for(int i=0; i<this.objNames.length; i++){
			this.objNames[i] = ((Cl2CmpObjective)objectives.get(i)).getName();
			objectiveNames += "-" + this.objNames[i];
		}
		// Remove first character
		objectiveNames = objectiveNames.substring(1);

		// Create the report title using the names of the strategy, dataset and objectives
		String strategyName = algorithm.getStrategy().getClass().getName();
		strategyName = strategyName.substring(strategyName.lastIndexOf(".")+1);
		if(strategyName.contains("Constrained"))
			strategyName = strategyName.substring(11);
		String problemInstance = ((Cl2CmpMoEvaluator) algorithm.getEvaluator()).getDatasetFileName();
		problemInstance = problemInstance.substring(problemInstance.lastIndexOf("/")+1,problemInstance.lastIndexOf("."));
		
		// Report title
		this.reportTitle = strategyName + "-" + problemInstance + "-" + objectiveNames;
		
		// Directory name
		this.dirname = "res/" + objectiveNames + "/" + problemInstance + "/res-pop";
	}
}