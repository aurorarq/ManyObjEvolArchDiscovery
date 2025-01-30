package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;

//import es.uco.kdis.datapro.dataset.InstanceIterator;
//import es.uco.kdis.datapro.dataset.Column.ColumnAbstraction;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;

public class ExtractParetoResults {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\2obj";
		//String resdirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-auto\\2obj";
		
		String dirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\9obj";
		String resdirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-auto\\9obj+nsga3";
		
		
		//G:\Aurora\Documentos\Universidad\KDIS\revistas\2014 Comparativa many\experimentación\rawdata2\2obj
		int nObjectives = 9;

		File dir = new File(dirname);
		File resdir = new File(resdirname);
		File [] objDir = dir.listFiles();
		File [] problemsDir;
		String [] algs = new String[]{"SPEA2", "NSGA2", "Cl2CmpMOEAD",
				"SSeMOEA", "GrEA", "IBEAe", "HypE", "NSGA3"};
		String [] cols = new String[]{"#Pareto", "Time(ms)", "#InvPop", "#Components", "Connectors"};
		String [] objs;
		String name;

		// Datasets
		String name1, name2, resname;
		ExcelDataset dataset1, dataset2, resdataset;
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

		// Combination of objectives
		File resfile;
		for(File f1: objDir){
			problemsDir = f1.listFiles();

			resname = "res-"+f1.getName()+".xlsx";
			resfile = new File(resdirname + "/" + resname);
			if (!resfile.exists()){
				// Results dataset
				objs = f1.getName().split("-");
				resdataset = new ExcelDataset();
				resdataset.addColumn(new NominalColumn("algorithm-problem"));
				for(int i=0; i<cols.length; i++)
					resdataset.addColumn(new NumericalColumn("avg-"+cols[i]));
				for(int i=0; i<nObjectives; i++)
					resdataset.addColumn(new NumericalColumn("avg-"+objs[i]));
				for(int i=0; i<cols.length; i++)
					resdataset.addColumn(new NumericalColumn("sd-"+cols[i]));
				for(int i=0; i<nObjectives; i++)
					resdataset.addColumn(new NumericalColumn("sd-"+objs[i]));

				/*for(ColumnAbstraction c: resdataset.getColumns()){
				System.out.println(c.getName());
			}*/

				// Algorithms
				for(String s: algs){

					// Problem instances
					for(File f2: problemsDir){
						name1 = f2.getAbsolutePath() + "/res-pop/" + s + "-" + f2.getName() + "-" + f1.getName() + "-pareto.xlsx";
						name2 = f2.getAbsolutePath() + "/res-pop/" + s + "-" + f2.getName() + "-" + f1.getName() + "-population.xlsx";

						// Open dataset (pareto)
						System.out.println(name1);
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
								// Save mean and stdev results
								name = s + "-" + f2.getName();
								resdataset.getColumn(0).addValue(name);	// algorithm + problem instance

								try{
									// Mean values
									resdataset.getColumn(1).addValue(dataset1.getColumn(1).getElement(30)); // #pareto
									resdataset.getColumn(2).addValue(dataset1.getColumn(2).getElement(30)); // time
									resdataset.getColumn(3).addValue(dataset2.getColumn(3).getElement(30)); // invalids
									resdataset.getColumn(4).addValue(dataset1.getColumn(3).getElement(30)); // components
									resdataset.getColumn(5).addValue(dataset1.getColumn(4).getElement(30)); // connectors
									// objectives
									int col = 6;
									for(int i=0; i<nObjectives; i++){
										resdataset.getColumn(col).addValue(dataset1.getColumnByName(objs[i]).getElement(30));
										col++;
									}
									// Standard deviation values
									resdataset.getColumn(col).addValue(dataset1.getColumn(1).getElement(31)); // #pareto
									resdataset.getColumn(col+1).addValue(dataset1.getColumn(2).getElement(31)); // time
									resdataset.getColumn(col+2).addValue(dataset2.getColumn(3).getElement(31)); // invalids
									resdataset.getColumn(col+3).addValue(dataset1.getColumn(3).getElement(31)); // components
									resdataset.getColumn(col+4).addValue(dataset1.getColumn(4).getElement(31)); // connectors
									// objectives
									col+=5;
									for(int i=0; i<nObjectives; i++){
										resdataset.getColumn(col).addValue(dataset1.getColumnByName(objs[i]).getElement(31));
										col++;
									}

								}catch(Exception e){
									System.out.println("Problema con valores en: " + dataset1.getFileName());
								}
							}
							else{
								// Save mean and stdev results
								name = s + "-" + f2.getName();
								resdataset.getColumn(0).addValue(name);	// algorithm + problem instance

								//System.out.println("Faltan ejecuciones en: " + dataset1.getFileName());
								// Mean values
								resdataset.getColumn(1).addValue(((NumericalColumn)dataset1.getColumn(1)).mean()); // #pareto
								resdataset.getColumn(2).addValue(((NumericalColumn)dataset1.getColumn(2)).mean()); // time
								resdataset.getColumn(3).addValue(((NumericalColumn)dataset2.getColumn(3)).mean()); // invalids
								resdataset.getColumn(4).addValue(((NumericalColumn)dataset1.getColumn(3)).mean()); // components
								resdataset.getColumn(5).addValue(((NumericalColumn)dataset1.getColumn(4)).mean()); // connectors
								// objectives
								int col = 6;
								for(int i=0; i<nObjectives; i++){
									resdataset.getColumn(col).addValue(((NumericalColumn)dataset1.getColumnByName(objs[i])).mean());
									col++;
								}
								// Standard deviation values
								resdataset.getColumn(col).addValue(((NumericalColumn)dataset1.getColumn(1)).standardDeviation()); // #pareto
								resdataset.getColumn(col+1).addValue(((NumericalColumn)dataset1.getColumn(2)).standardDeviation()); // time
								resdataset.getColumn(col+2).addValue(((NumericalColumn)dataset2.getColumn(3)).standardDeviation()); // invalids
								resdataset.getColumn(col+3).addValue(((NumericalColumn)dataset1.getColumn(3)).standardDeviation()); // components
								resdataset.getColumn(col+4).addValue(((NumericalColumn)dataset1.getColumn(4)).standardDeviation()); // connectors
								// objectives
								col+=5;
								for(int i=0; i<nObjectives; i++){
									resdataset.getColumn(col).addValue(((NumericalColumn)dataset1.getColumnByName(objs[i])).standardDeviation());
									col++;
								}
							}
						}
					}
				}

				/*for(ColumnAbstraction c: resdataset.getColumns()){
				System.out.println(c.getName() + " " + c.getSize());
			}
			InstanceIterator it = new InstanceIterator(resdataset);
			while(!it.isDone()){
				System.out.println(it.currentInstance());
				it.next();
			}*/

				// Save dataset

				resdataset.setMissingValue("-");
				resdataset.setNullValue("-");
				try {
					((ExcelDataset)resdataset).writeDataset(resdirname + "/" + resname);
				} catch (IOException e) {
					System.out.println("Problems saving the results dataset: " + resname);
					e.printStackTrace();
				}
			}
		}
	}
}