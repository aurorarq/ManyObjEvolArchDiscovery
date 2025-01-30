package net.sf.jclec.sbse.cl2cmp.mo.listener;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.MOAlgorithm;
import net.sf.jclec.mo.strategies.constrained.IConstrained;
import net.sf.jclec.sbse.cl2cmp.mo.algorithms.Cl2CmpMoAlgorithm;

/**
 * Population reporter for the
 * CL2CMP multiobjective problem.
 * It stores information about
 * the final population for
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
public class PopulationReporter extends Cl2CmpReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////	

	/** Serial ID */
	private static final long serialVersionUID = -7563461571517775497L;

	/** Dataset for size distribution */
	private Dataset sizeDataset;

	/** Minimum number of components */
	private int min;

	/** Maximum number of components */
	private int max;
	
	/** Title of the second report (components frequency) */
	private String reportTitleSize;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty Constructor
	 * */
	public PopulationReporter() {
		super();
		this.colsNames = new String[]{"Avg #Components", "Avg #Connectors", "#Invalids"};
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		// Call super initialization
		super.algorithmStarted(event);

		Cl2CmpMoAlgorithm algorithm = ((Cl2CmpMoAlgorithm)event.getAlgorithm());
		this.min = algorithm.getMinNumberOfComponents();
		this.max = algorithm.getMaxNumberOfComponents();

		// First execution
		if(this.isFirstExecution){
			this.sizeDataset = new ExcelDataset();
			this.sizeDataset.addColumn(new NominalColumn("Execution"));
			for(int i=this.min; i<=this.max; i++)
				this.sizeDataset.addColumn(new NumericalColumn(i + " comp"));
		}

		// Other executions, open the dataset
		else{
			this.sizeDataset = new ExcelDataset(dirname + "/" + this.reportTitleSize + ".xlsx");
			int numOfCols = this.max-this.min+1;
			String format = "s";	// The first column is nominal
			// Concatenate 'f' for numerical columns
			for(int i=0; i<numOfCols; i++)
				format = format.concat("f");
			this.sizeDataset.setNullValue("?");
			try{
				((ExcelDataset)this.sizeDataset).readDataset("nv", format);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmFinished(AlgorithmEvent event) {
		
		// Call super method to do the last report
		// and save the results dataset
		super.algorithmFinished(event);
		
		// Save the specific dataset
		try {
			// If it is the last execution, compute mean and standard deviation
			int size = this.sizeDataset.getColumn(0).getSize();
			if(this.numberOfExectutions>1 && size==this.numberOfExectutions){
				finalStatistics(this.sizeDataset);
			}
			((ExcelDataset)this.sizeDataset).writeDataset(dirname + "/" + this.reportTitleSize + ".xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doReport(MOAlgorithm algorithm) {
		// Population individuals
		List<IIndividual> inhabitants = algorithm.getInhabitants();

		// Do population report

		// Statistics: only with valid individuals
		List<IIndividual> validInds = new ArrayList<IIndividual>();
		for(IIndividual ind: inhabitants)
			if(((IConstrained)ind).isFeasible())
				validInds.add(ind);

		// Current execution
		String execution = "Execution " + this.currentExecution;
		this.resultsDataset.getColumn(0).addValue(execution);
		
		// Average metrics in population (only with valid individuals)
		double aux = averageComponents(validInds);
		this.resultsDataset.getColumn(1).addValue(aux);
		aux = averageConnectors(validInds);
		this.resultsDataset.getColumn(2).addValue(aux);
		// Number of invalids
		this.resultsDataset.getColumn(3).addValue((double)((Cl2CmpMoAlgorithm)algorithm).getNumberOfInvalids());
		
		// Average value of each fitness metrics
		int j=4;
		for(int i=0; i<this.objNames.length; i++){
			aux = mean(validInds, i);
			this.resultsDataset.getColumn(j).addValue(aux);
			j++;
		}
		
		// Save values in the size dataset
		this.sizeDataset.getColumn(0).addValue(execution);
		int [] compFreq = ((Cl2CmpMoAlgorithm)algorithm).getComponentFrequency();
		for(int i=0; i<compFreq.length; i++){
			this.sizeDataset.getColumn(i+1).addValue((double)compFreq[i]);
		}
		
		this.currentExecution++;
	}
	
	@Override
	protected void createReportTitle(Cl2CmpMoAlgorithm algorithm){
		super.createReportTitle(algorithm);
		this.reportTitleSize = this.reportTitle + "-frequency";
		this.reportTitle += "-population";
	}
}
