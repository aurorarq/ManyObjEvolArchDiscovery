package net.sf.jclec.sbse.cl2cmp.mo.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.MOAlgorithm;
import net.sf.jclec.mo.fitness.Objective;
import net.sf.jclec.mo.fitness.MOFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoEvaluator;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.sbse.cl2cmp.mo.algorithms.Cl2CmpMoAlgorithm;
import net.sf.jclec.sbse.cl2cmp.mo.objectives.Cl2CmpObjective;

/**
 * This listener stores in a file the individuals
 * in the pareto front at the end of the algorithm 
 * execution.
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (May 2014)</li>
 * </ul>
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class IndividualsFileReporter extends Cl2CmpReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2564280495424544092L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	public IndividualsFileReporter(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	@Override
	public void configure(Configuration settings) {
		// Do nothing
	}

	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		Cl2CmpMoAlgorithm algorithm = (Cl2CmpMoAlgorithm)event.getAlgorithm();
		createReportTitle(algorithm);
		
		reportDirectory = new File(dirname);
		if(!reportDirectory.exists()){
			reportDirectory.mkdirs();
		}
	}

	@Override
	public void iterationCompleted(AlgorithmEvent event) {
		// Do nothing
	}

	@Override
	public void algorithmFinished(AlgorithmEvent event) {
		doReport((MOAlgorithm)event.getAlgorithm());
	}

	@Override
	public void algorithmTerminated(AlgorithmEvent e) {
		// Do nothing
	}

	@Override
	protected void doReport(MOAlgorithm algorithm) {
		File file;
		FileWriter writer;
		MOFitness fitness;
		int nFiles = reportDirectory.listFiles().length;

		String filename = dirname+"/"+reportTitle+"-ex"+nFiles+".txt";

		file = new File(filename);
		if(!file.exists()){
			if(file.getParentFile()!=null)
				file.getParentFile().mkdirs();
		}

		try {
			List<IIndividual> front = ((Cl2CmpMoAlgorithm)algorithm).getFinalIndividuals();
			// Get individuals objectives
			writer = new FileWriter(file, true);
			StringBuffer buffer = new StringBuffer();
			double value;
			for (IIndividual ind : front) {
				fitness  = (MOFitness)ind.getFitness();
				writer.append(((Cl2CmpMoIndividual)ind).toString() + "\n");
				for(int i=0; i<objNames.length; i++){
					value = fitness.getComponentValue(i);
					if(value<0)
						value=value*(-1);
					writer.append("\t" + objNames[i] + ": " + value + "\n");
				}
				writer.append("--\n");
			}
			writer.flush();
			writer.write(buffer+"-------------------------\n");
			writer.close();
		} catch (IOException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Override
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
		this.dirname = "res/" + objectiveNames + "/" + problemInstance + "/res-inds/" + strategyName;
	}
}