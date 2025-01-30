package net.sf.jclec.sbse.cl2cmp.mo.listener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.MOAlgorithm;
import net.sf.jclec.mo.fitness.Objective;
import net.sf.jclec.mo.fitness.MOFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoEvaluator;
import net.sf.jclec.sbse.cl2cmp.mo.algorithms.Cl2CmpMoAlgorithm;
import net.sf.jclec.sbse.cl2cmp.mo.objectives.Cl2CmpObjective;
import net.sf.jclec.mo.MOEvaluator;

/**
 * Reporter that stores the solutions
 * composing the pareto front in an
 * excel file.
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (May 2014)</li>
 * </ul>
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class IndividualsFrontExcelReporter extends Cl2CmpReporter {

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------------- Properties
	//////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -779258708157546429L;

	private int nInitial;
	
	//////////////////////////////////////////////////////////////////
	//----------------------------------------------------- Properties
	//////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public IndividualsFrontExcelReporter(){
		super();
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	@Override
	public void configure(Configuration settings) {
		// Do nothing
		this.nInitial = settings.getInt("initial",0);
	}

	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		Cl2CmpMoAlgorithm algorithm = (Cl2CmpMoAlgorithm)event.getAlgorithm();
		createReportTitle(algorithm);

		// Create an empty dataset
		List<Objective> objectives = ((MOEvaluator)algorithm.getEvaluator()).getObjectives();
		int numberOfObjectives = objectives.size();
		this.resultsDataset = new ExcelDataset();
		// Add column (one for each objective)
		for(int i=0; i<numberOfObjectives; i++)
			this.resultsDataset.addColumn(new NumericalColumn(((Cl2CmpObjective)objectives.get(i)).getName()));

	}

	@Override
	public void iterationCompleted(AlgorithmEvent event) {
		// Do nothing
	}

	@Override
	public void algorithmFinished(AlgorithmEvent event) {

		this.reportDirectory = new File(dirname);
		if(!this.reportDirectory.exists()){
			this.reportDirectory.mkdirs();
		}

		doReport((MOAlgorithm)event.getAlgorithm());

		// Save dataset with the number of current execution
		int nFiles = this.reportDirectory.listFiles().length +1;
		try {
			((ExcelDataset)this.resultsDataset).writeDataset(this.dirname+"/"+ this.reportTitle+"-ex"+(nFiles+nInitial)+".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void algorithmTerminated(AlgorithmEvent e) {
		// Do nothing
	}

	@Override
	protected void doReport(MOAlgorithm algorithm) {
		List<IIndividual> front = ((Cl2CmpMoAlgorithm)algorithm).getFinalIndividuals();
		int size = front.size();
		int numOfObjectives = ((MOEvaluator)algorithm.getEvaluator()).getObjectives().size();
		MOFitness fitness;

		// Set each fitness value as a new instance of the dataset
		for(int i=0; i<size; i++){
			fitness = (MOFitness)front.get(i).getFitness();
			for(int j=0; j<numOfObjectives; j++){
				try {
					this.resultsDataset.getColumn(j).addValue(fitness.getComponentValue(j));
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////
	//--------------------------------------------- Protected methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Create report title and the
	 * directory name using the
	 * parameters of the algorithm
	 * @param algorithm The executed algorithm
	 * */
	protected void createReportTitle(Cl2CmpMoAlgorithm algorithm){
		// Names of the objectives
		List<Objective> objectives = ((Cl2CmpMoEvaluator)algorithm.getEvaluator()).getObjectives();
		this.objNames = new String[objectives.size()];
		String objectiveNames = "";
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
		this.dirname = "res/" + objectiveNames + "/" + problemInstance + "/res-fronts/" + strategyName;
	}
}
