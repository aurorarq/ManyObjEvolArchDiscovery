package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.distance.EuclideanDistance;
import net.sf.jclec.mo.fitness.MOFitness;
import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.InstanceIterator;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class NormalizeReduce {

	protected String dirname;
	protected String resFrontsDirname = "/fronts/";
	protected String resNormDirname = "/fronts-norm/";
	protected String resSolutions = "/pareto-example/";
	protected int numOfObjectives;
	protected int externalSize;
	protected int kValue;
	
	
	public NormalizeReduce(String dirname, int nObjs, int externalSize, int kValue) {
		this.dirname = dirname;
		this.numOfObjectives = nObjs;
		this.externalSize = externalSize;
		this.kValue = kValue;
	}

	public static void main(String[] args) {

		//NormalizeIndicators obj = new NormalizeIndicators(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\gráficos";
		int objs = 9, externalSize = 20, kValue = 12;
		NormalizeReduce obj = new NormalizeReduce(dirname,objs,externalSize,kValue);
		obj.normalize();
		obj.reduce();
	}

	public void normalize(){
		File dir1;
		Dataset dataset;
		NumericalColumn col;
		double minCol, maxCol, value;
		int size;
		double [] min = new double[numOfObjectives];
		double [] max = new double[numOfObjectives];

		for(int i=0; i<numOfObjectives; i++){
			min[i] = Double.POSITIVE_INFINITY;
			max[i] = Double.NEGATIVE_INFINITY;
		}

		String format = "";
		for(int i=0; i<numOfObjectives; i++)
			format+="f";

		// Get the minimum and maximum value for each objective
		// from all the non-dominated solutions found by all
		// the algorithms
		File resFrontsNorDir;
		File [] files;
		boolean empty = false;

		dir1 = new File(dirname+"/"+resFrontsDirname);

		if(dir1.exists()){

			// Create the directory of results
			resFrontsNorDir = new File(dirname+"/"+this.resNormDirname);
			if(resFrontsNorDir.exists()){
				File [] f2 = resFrontsNorDir.listFiles();
				for(File f: f2)
					f.delete();
			}
			else
				resFrontsNorDir.mkdirs();

			// Clean values
			for(int i=0; i<numOfObjectives; i++){
				min[i] = Double.POSITIVE_INFINITY;
				max[i] = Double.NEGATIVE_INFINITY;
			}

			// Open each dataset containing a front and compute the
			// minimum and maximum values of each objective
			files = dir1.listFiles();

			for(File f: files){

				// Open dataset
				dataset = new ExcelDataset(f.getAbsolutePath());
				empty=false;
				try {
					((ExcelDataset)dataset).readDataset("nv", format);
				} catch (IndexOutOfBoundsException | IOException
						| NotAddedValueException
						| IllegalFormatSpecificationException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				catch(InputMismatchException e){
					empty=true;
				}

				// Minimum and maximum
				if(!empty){
					for(int i=0; i<numOfObjectives; i++){
						col = (NumericalColumn)dataset.getColumn(i);
						minCol = col.getMinValue();
						maxCol = col.getMaxValue();

						if(minCol < min[i])
							min[i] = minCol;
						if(maxCol > max[i])
							max[i] = maxCol;
					}
				}
			}

			for(int i=0; i<numOfObjectives; i++){
				if(max[i] == Double.NEGATIVE_INFINITY)
					max[i]=min[i];
				if(min[i] == Double.POSITIVE_INFINITY)
					min[i]=max[i];
				if(max[i]==-0.0)
					max[i]=0.0;
				if(min[i]==-0.0)
					min[i]=0.0;
			}
		}// end min/max computation

		// Now, normalize the values of each solution in each front
		// and saved it into the normalized fronts directory

		files = dir1.listFiles();
		for(File f: files){
			// Open dataset
			dataset = new ExcelDataset(f.getAbsolutePath());
			empty=false;
			try {
				((ExcelDataset)dataset).readDataset("nv", format);
			} catch (IndexOutOfBoundsException | IOException
					| NotAddedValueException
					| IllegalFormatSpecificationException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			catch(InputMismatchException e){
				empty=true;
			}

			// Normalize each value
			if(!empty){
				for(int i=0; i<numOfObjectives; i++){
					col = (NumericalColumn)dataset.getColumn(i);
					size = col.getSize();
					for(int j=0; j<size; j++){
						if(max[i]!=min[i]){
							value = (((Double)col.getElement(j)).doubleValue() - min[i])/(max[i]-min[i]);
						}
						else{
							value = 1.0;
						}
						col.setValue(value, j);
					}
				}
			}

			// Save the dataset
			String name2 = this.dirname + "/" + this.resNormDirname + "/norm-" + f.getName();
			try {
				((ExcelDataset)dataset).writeDataset(name2);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}

		}
	}
	
	public void reduce(){
		//String [] algs = new String []{"SPEA2", "IBEAe"};
		
		String [] algs = new String []{"SPEA2", "NSGA2", "SSeMOEA", "GrEA", "IBEAe", "HypE", "Cl2CmpMOEAD"};
		
		
		File dir1 = new File(this.dirname+"/"+this.resNormDirname);
		File [] files = dir1.listFiles();
		Dataset dataset;
		ArrayList<IIndividual> solutions = new ArrayList<IIndividual>();
		MOFitness fitness;
		SimpleIndividual ind;
		InstanceIterator it;
		List<Object> values;
		SimpleValueFitness [] dValues;
		
		String format = "";
		for(int i=0; i<numOfObjectives; i++)
			format+="f";
		
		File dir2 = new File(this.dirname+"/"+this.resSolutions);
		if(!dir2.exists())
			dir2.mkdirs();
		
		// For each algorithm
		for(String alg: algs){
			
			solutions.clear();
			fitness = null;
			dValues = null;
			
			// Copy solutions -> individuals
			for(File f: files){
				
				// Copy all the solutions of the algorithm
				if(f.getName().contains(alg)){
					
					// Open dataset
					try {
						dataset = new ExcelDataset(f.getAbsolutePath());
						((ExcelDataset)dataset).readDataset("nv", format);
						
						it = new InstanceIterator(dataset);
						while(!it.isDone()){
							values = it.currentInstance();
							
							// Create a fitness
							fitness = new MOFitness();
							dValues = new SimpleValueFitness[values.size()];
							for(int i=0; i<values.size(); i++){
								dValues[i] = new SimpleValueFitness(((Double)values.get(i)).doubleValue());
							}
							fitness.setComponents(dValues);
							ind = new SimpleIndividual();
							ind.setFitness(fitness.copy());
							solutions.add(ind.copy());
							
							it.next();
						}

					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException
							| IllegalFormatSpecificationException e) {
						e.printStackTrace();
						System.exit(-1);
					}
					
				}
			}
			
			// Reduce the archive
			List<IIndividual> selected;
			if(solutions.size() > this.externalSize){
				selected = decrementPopulation(solutions, solutions.size());
			}
			else
				selected = solutions;
			
			// Save the solutions
			Dataset res = new ExcelDataset();
			String colnames [] = new String[]{"ICD","ERP","GCR","INS","CS","ENC","CL","CB","ABS"};
			for(int i=0; i<numOfObjectives; i++){
				res.addColumn(new NumericalColumn(colnames[i]));
			}
			for(IIndividual individual: selected){
				fitness = (MOFitness) individual.getFitness();
				for(int j=0; j<fitness.getNumberOfComponents(); j++){
					try {
						res.getColumn(j).addValue(fitness.getComponentValue(j));
					} catch (IllegalAccessException|IllegalArgumentException e) {
						e.printStackTrace();
					}
				}	
			}
			String resname = dir2.getAbsolutePath() + "/" + alg + ".xlsx";
			try {
				((ExcelDataset)res).writeDataset(resname);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected List<IIndividual> decrementPopulation(List<IIndividual> archive, int currentSize){

		double [] distance = new double[currentSize];
		double [] k_distance = new double[currentSize];
		double auxDistance;
		int [] k_nearest = new int[currentSize];
		int [] sortedIndividuals = new int[currentSize];
		int auxSortedIndividual;

		for(int i=0; i<currentSize; i++){
			k_distance[i] = Double.MAX_VALUE;
			k_nearest[i] = i;
		}
		EuclideanDistance euclDistance = new EuclideanDistance();
		while(currentSize > externalSize){
			for(int i=0; i<currentSize; i++){
				for(int j=0; j<currentSize; j++){	
					distance[j] = euclDistance.distance(archive.get(i),archive.get(j));
					sortedIndividuals[j] = j;
				}

				//Sort the individuals with respect to its distance
				//to individual 'i'
				for(int j=0; j<currentSize-1; j++)
					for(int l=j+1; l<currentSize; l++)
						if(distance[j] > distance[l]){

							auxDistance = distance[j];
							distance[j] = distance[l];
							distance[l] = auxDistance;

							auxSortedIndividual = sortedIndividuals[j];
							sortedIndividuals[j] = sortedIndividuals[l];
							sortedIndividuals[l] = auxSortedIndividual;
						}

				k_nearest[i] = sortedIndividuals[kValue];
				k_distance[i] = distance[kValue];
			}

			//Make the ordering having present the k_distance
			//with k_nearest neighbor calculated in the previous step
			for(int j=0; j<currentSize; j++)
				sortedIndividuals[j] = j;

			for(int j=0; j<currentSize-1; j++)
				for(int l=j+1; l<currentSize; l++)
					if(k_distance[j] > k_distance[l]){
						auxDistance = k_distance[j];
						k_distance[j] = k_distance[l];
						k_distance[l] = auxDistance;

						auxSortedIndividual = sortedIndividuals[j];
						sortedIndividuals[j] = sortedIndividuals[l];
						sortedIndividuals[l] = auxSortedIndividual;
					}
			//Select closet individual from the k-th neighbor
			archive.remove(k_nearest[sortedIndividuals[0]]);
			currentSize --;
		}
		return archive;
	}
}
