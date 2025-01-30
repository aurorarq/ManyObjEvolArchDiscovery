package net.sf.jclec.sbse.cl2cmp.mo.listener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.mo.MOAlgorithm;
import net.sf.jclec.mo.fitness.Objective;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoEvaluator;
import net.sf.jclec.sbse.cl2cmp.mo.algorithms.Cl2CmpMoAlgorithm;
import net.sf.jclec.sbse.cl2cmp.mo.objectives.Cl2CmpObjective;

/**
 * Reporter that stores metrics
 * about the evolution process
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (June 2014)</li>
 * </ul>
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class EvolutionReporter extends Cl2CmpReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8751316754509903535L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty Constructor
	 * */
	public EvolutionReporter() {
		super();
		this.colsNames = new String[]{"Evaluations", "#Invalids", "#NonDominated", "#Progress"};
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////	

	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		Cl2CmpMoAlgorithm algorithm = (Cl2CmpMoAlgorithm)event.getAlgorithm();
		createReportTitle(algorithm);

		// Create an empty dataset with the required columns
		this.resultsDataset = new ExcelDataset();
		for(int i=0; i<colsNames.length; i++)
			this.resultsDataset.addColumn(new NumericalColumn(colsNames[i]));

		// Columns for component frequency
		int min = algorithm.getMinNumberOfComponents();
		int max = algorithm.getMaxNumberOfComponents();
		for(int i=min; i<=max; i++){
			this.resultsDataset.addColumn(new NumericalColumn(i+"-comp"));
		}
		
		// First report
		doReport(algorithm);
	}

	@Override
	public void iterationCompleted(AlgorithmEvent event) {
		MOAlgorithm alg = (Cl2CmpMoAlgorithm)event.getAlgorithm();
		int evaluation = alg.getEvaluator().getNumberOfEvaluations();
		if (evaluation%reportFrequency == 0) {
			doReport(alg);
		}
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
			((ExcelDataset)this.resultsDataset).writeDataset(this.dirname+"/"+ this.reportTitle+"-ex"+nFiles+".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doReport(MOAlgorithm algorithm) {

		// Get information about the evolution process
		int evaluations = algorithm.getEvaluator().getNumberOfEvaluations();
		int invalids = ((Cl2CmpMoAlgorithm)algorithm).getNumberOfInvalids();
		int nonDominated = ((Cl2CmpMoAlgorithm)algorithm).getNumberOfNonDominated();
		int progress = ((Cl2CmpMoAlgorithm)algorithm).getProgress();
		int frequency [] = ((Cl2CmpMoAlgorithm)algorithm).getComponentFrequency();

		// Save in the dataset
		this.resultsDataset.getColumn(0).addValue((double)evaluations);
		this.resultsDataset.getColumn(1).addValue((double)invalids);
		this.resultsDataset.getColumn(2).addValue((double)nonDominated);
		this.resultsDataset.getColumn(3).addValue((double)progress);
		int j=4;
		for(int i=0; i<frequency.length; i++){
			this.resultsDataset.getColumn(j).addValue((double)frequency[i]);
			j++;
		}
	}

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
		this.dirname = "res/" + objectiveNames + "/" + problemInstance + "/res-evol/" + strategyName;
	}
}
