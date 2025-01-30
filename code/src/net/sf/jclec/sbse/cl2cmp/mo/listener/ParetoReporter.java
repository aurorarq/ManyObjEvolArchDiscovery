package net.sf.jclec.sbse.cl2cmp.mo.listener;

import java.util.List;
import es.uco.kdis.datapro.datatypes.NullValue;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.MOAlgorithm;
import net.sf.jclec.sbse.cl2cmp.mo.algorithms.Cl2CmpMoAlgorithm;

/**
 * Pareto reporter for the
 * CL2CMP multiobjective problem.
 * It stores information about
 * the found pareto front for
 * multiple executions.
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (May 2014) </li>
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class ParetoReporter extends Cl2CmpReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 4576724876954668543L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty Constructor
	 * */
	public ParetoReporter() {
		super();
		this.colsNames = new String[]{"#Pareto", "Time(ms)", "#Components", "#Connectors"};
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////	

	@Override
	protected void doReport(MOAlgorithm algorithm) {

		// Get pareto front
		List<IIndividual> pareto = ((Cl2CmpMoAlgorithm)algorithm).getFinalIndividuals();

		// Save the values in the dataset
		String execution = "Execution " + this.currentExecution;
		this.resultsDataset.getColumn(0).addValue(execution);

		// Pareto front size
		this.resultsDataset.getColumn(1).addValue((double)pareto.size());

		// Execution time
		this.resultsDataset.getColumn(2).addValue((double)((Cl2CmpMoAlgorithm)algorithm).getTime());

		// Non dominated solutions
		if(pareto.size()>0){
			this.resultsDataset.getColumn(3).addValue(averageComponents(pareto));
			this.resultsDataset.getColumn(4).addValue(averageConnectors(pareto));

			// Average value of each fitness metrics
			int j = 5;
			for(int i=0; i<this.objNames.length; i++){
				this.resultsDataset.getColumn(j).addValue(mean(pareto,i));
				j++;
			}
		}
		// Pareto front is empty
		else{
			this.resultsDataset.getColumn(1).addValue(0.0);
			this.resultsDataset.getColumn(3).addValue(NullValue.getNullValue());
			this.resultsDataset.getColumn(4).addValue(NullValue.getNullValue());

			int j = 5;
			for(int i=0; i<this.objNames.length; i++){
				this.resultsDataset.getColumn(j).addValue(NullValue.getNullValue());
				j++;
			}
		}
	}
	
	@Override
	protected void createReportTitle(Cl2CmpMoAlgorithm algorithm){
		super.createReportTitle(algorithm);
		this.reportTitle += "-pareto";
	}
}
