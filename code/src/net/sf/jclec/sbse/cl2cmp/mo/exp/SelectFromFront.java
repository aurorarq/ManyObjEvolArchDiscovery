package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.CsvDataset;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class SelectFromFront {


	public static void main(String args[]){


		String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\gráficos\\fronts-norm-aqualush-nsga3";
		int numOfObjectives = 9;
		int numOfSolutions = 50;

		File dir = new File(dirname);
		File [] files = dir.listFiles();
		Dataset dataset = null, algDataset;
		String [] algs = new String []{"SPEA2", "NSGA2", "Cl2CmpMOEAD", "SSeMOEA", "GrEA", "IBEAe", "HypE", "NSGA3"};
		String format = "";
		NumericalColumn col;
		double  [] min = new double[numOfObjectives];
		double [] max = new double[numOfObjectives];
		int [] minPos = new int[numOfObjectives];
		int [] maxPos = new int[numOfObjectives];
		int numInstances, currentInstances;
		double value;
		int index;
		boolean finish;
		Random randomGener = new Random(System.currentTimeMillis());
		String resname;
		ArrayList<Integer> indexes = new ArrayList<Integer>();

		for(int i=0; i<numOfObjectives; i++)
			format+="f";

		// For each algorithm
		for(String alg: algs){

			// A dataset for each algorithm
			algDataset = new ExcelDataset();
			for(int i=0; i<numOfObjectives; i++){
				algDataset.addColumn(new NumericalColumn());
			}

			// Copy solutions from different executions
			for(File f: files){
				
				if(f.getName().contains(alg)){

					try {
						//dataset = new ExcelDataset(f.getAbsolutePath());
						//((ExcelDataset)dataset).readDataset("nv", format);
						dataset = new CsvDataset(f.getAbsolutePath());
						((CsvDataset)dataset).readDataset("nv", format);

						for(int i=0; i<numOfObjectives; i++){
							col = (NumericalColumn)dataset.getColumn(i);
							algDataset.getColumn(i).addAllValues(col.getValues());
						}

					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException
							| IllegalFormatSpecificationException e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}
			}

			// Copy column names
			/*for(int i=0; i<numOfObjectives; i++){
				algDataset.getColumn(i).setName(dataset.getColumn(i).getName());
			}*/

			numInstances = algDataset.getColumn(0).getSize();
			resname = dir.getAbsolutePath() + "/" + alg + ".xlsx";

			if(numInstances < numOfSolutions){
				dataset = algDataset;
			}

			else{

				// Min and max for each objective
				for(int i=0; i<numOfObjectives; i++){
					minPos[i]=maxPos[i]=-1;
					min[i]=1.1;
					max[i]=-1.0;
				}


				for(int i=0; i<numInstances; i++){

					for(int j=0; j<numOfObjectives; j++){
						col = (NumericalColumn)algDataset.getColumn(j);
						value = ((Double)col.getElement(i)).doubleValue();

						if(value < min[j]){
							min[j] = value;
							minPos[j] = i;
						}
						if(value > max[j]){
							max[j] = value;
							maxPos[j] = i;
						}
					}
				}

				// Remove duplicated index
				for(int i=0; i<numOfObjectives; i++){
					for(int j=0; j<numOfObjectives; j++){
						if(minPos[i]==maxPos[j]){
							minPos[i] = -1;
						}
					}
				}
				for(int i=0; i<numOfObjectives-1; i++){
					if(maxPos[i]==maxPos[i+1]){
							maxPos[i] = -1;
						}
				}
				
				// Copy min and max instances
				dataset = algDataset.clone();
				for(int i=0; i<numOfObjectives; i++){
					for(int j=0; j<numOfObjectives; j++){
						if(maxPos[i]!=-1)
							dataset.getColumn(j).addValue(algDataset.getColumn(j).getElement(maxPos[i]));
						if(minPos[i]!=-1)
							dataset.getColumn(j).addValue(algDataset.getColumn(j).getElement(minPos[i]));
					}
				}

				// Copy random instances
				currentInstances = dataset.getColumn(0).getSize();

				while(currentInstances<numOfSolutions){

					index = (int)(randomGener.nextDouble()*numInstances);
					finish=false;
					for(int i=0; i<numOfObjectives && !finish; i++){
						if(index == minPos[i] || index == maxPos[i]){
							index=-1;
							finish = true;
						}
					}
					if(!finish && !indexes.contains(index)){
						for(int i=0; i<numOfObjectives; i++){
							dataset.getColumn(i).addValue(algDataset.getColumn(i).getElement(index));
						}
						currentInstances++;
						indexes.add(index);
					}
				}
			}
			
			// Save dataset
			try {
				((ExcelDataset)dataset).writeDataset(resname);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
