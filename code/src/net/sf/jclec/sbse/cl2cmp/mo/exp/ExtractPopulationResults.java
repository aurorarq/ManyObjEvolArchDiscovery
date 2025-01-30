package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;

import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;

public class ExtractPopulationResults {

	public static void main(String [] args){
		//String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\2obj";
		//String resdirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-auto\\2obj";

		String dirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\9obj";
		String resdirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-auto\\9obj";


		//G:\Aurora\Documentos\Universidad\KDIS\revistas\2014 Comparativa many\experimentación\rawdata2\2obj
		int nObjectives = 9;

		File dir = new File(dirname);
		File resdir = new File(resdirname);
		File [] objDir = dir.listFiles();
		File [] problemsDir;
		/*String [] algs = new String[]{"SPEA2", "NSGA2", "Cl2CmpMOEAD",
				"SSeMOEA", "GrEA", "IBEAe", "HypE"};*/
		String [] algs = new String[]{"NSGA3"};
		double [] values = new double [3];	// invalids, time, number of solutions

		// Datasets
		String name1, name2;
		ExcelDataset dataset1, dataset2, datasetInvalids, datasetTime, datasetNumSolutions;
		boolean exist1, exist2;

		// Format of columns in the datasets
		String format1 = "sffff", format2 = "sfff";
		for(int i=0; i<nObjectives; i++){
			format1+="f";
			format2+="f";
		}

		// Results directory
		if(!resdir.exists()){
			resdir.mkdirs();
		}
	
		datasetInvalids = new ExcelDataset();
		datasetTime = new ExcelDataset();
		datasetNumSolutions = new ExcelDataset();
		for(int i=0; i<algs.length; i++){
			datasetInvalids.addColumn(new NumericalColumn(algs[i]));
			datasetNumSolutions.addColumn(new NumericalColumn(algs[i]));
			datasetTime.addColumn(new NumericalColumn(algs[i]));
		}
				
		// Combination of objectives
		//File resfile;
		for(File f1: objDir){
			problemsDir = f1.listFiles();

			//resname = "res-"+f1.getName()+".xlsx";
			//resfile = new File(resdirname + "/" + resname);
			//if (!resfile.exists()){
				
				// Algorithms
				for(String s: algs){

					// Problem instances
					for(File f2: problemsDir){
						name1 = f2.getAbsolutePath() + "/res-pop/" + s + "-" + f2.getName() + "-" + f1.getName() + "-pareto.xlsx";
						name2 = f2.getAbsolutePath() + "/res-pop/" + s + "-" + f2.getName() + "-" + f1.getName() + "-population.xlsx";

						// Open dataset (pareto)
						dataset1 = new ExcelDataset(name1);
						dataset1.setMissingValue("?");
						dataset1.setNullValue("#NUM!");

						try {
							dataset1.readDataset("nv", format1);
							exist1=true;
						} catch (Exception e) {
							//System.out.println("dataset1: " + name1);
							//e.printStackTrace();
							exist1=false;
						}

						dataset2 = new ExcelDataset(name2);
						dataset2.setMissingValue("?");
						dataset2.setNullValue("#NUM!");

						try {
							dataset2.readDataset("nv", format2);
							exist2=true;
						} catch (Exception  e) {
							//System.out.println("dataset2: " + name2);
							//e.printStackTrace();
							exist2=false;
						}

						if(exist1 && exist2){

							if (dataset1.getColumn(0).getSize() == 32){ // check size (30 executions)
								values[0] = (double)dataset2.getColumn(3).getElement(30); // invalids
								values[1] = (double)dataset1.getColumn(2).getElement(30); // time
								values[2] = (double)dataset1.getColumn(1).getElement(30); // number of solutions in the pareto set

							}
							else{
								values[0] = ((NumericalColumn)dataset2.getColumn(3)).mean(); // invalids
								values[1] = ((NumericalColumn)dataset1.getColumn(2)).mean(); // time
								values[2] = ((NumericalColumn)dataset1.getColumn(1)).mean(); // number of solutions in the pareto set
							}


							// Save results
							datasetInvalids.getColumnByName(s).addValue(values[0]);
							datasetTime.getColumnByName(s).addValue(values[1]);
							datasetNumSolutions.getColumnByName(s).addValue(values[2]);

						}
					}
				}
			//}
		}

		// Save datasets
		try {
			((ExcelDataset)datasetInvalids).writeDataset(resdirname + "/" + nObjectives + "objs-invalids-nsga3.xlsx");
			((ExcelDataset)datasetTime).writeDataset(resdirname + "/" + nObjectives + "objs-time-nsga3.xlsx");
			((ExcelDataset)datasetNumSolutions).writeDataset(resdirname + "/" + nObjectives + "objs-paretoSize-nsga3.xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
