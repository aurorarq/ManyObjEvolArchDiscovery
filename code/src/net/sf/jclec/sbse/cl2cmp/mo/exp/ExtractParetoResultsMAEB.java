package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;

//import es.uco.kdis.datapro.dataset.InstanceIterator;
//import es.uco.kdis.datapro.dataset.Column.ColumnAbstraction;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;

public class ExtractParetoResultsMAEB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\congresos\\2015_MAEB\\experimentación\\rawdata\\4obj\\5-icd-gcr-ins-en";
		String resdirname = "C:\\Users\\Aurora\\Documents\\KDIS\\congresos\\2015_MAEB\\experimentación\\res-auto\\";
		int nObjectives = 4;

		File dir = new File(dirname);
		File resdir = new File(resdirname);
		File [] objDir = dir.listFiles();

		String [] cols = new String[]{"#Pareto", "#Components", "Connectors", "Time(ms)", "#InvPop"};
		String [] objs;
		String [] aux;
		String name;

		// Datasets
		String name2, resname;
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

		objs = dirname.split("-"); 

		File resfile;
		resname = "res-"+dirname.substring(dirname.indexOf("-")-1)+".xlsx";
		resfile = new File(resdirname + "/" + resname);
		if (!resfile.exists()){
			// Results dataset
			resdataset = new ExcelDataset();
			resdataset.addColumn(new NominalColumn("algorithm-problem"));
			for(int i=0; i<cols.length; i++)
				resdataset.addColumn(new NumericalColumn("avg-"+cols[i]));
			for(int i=0; i<nObjectives; i++)
				resdataset.addColumn(new NumericalColumn("avg-"+objs[i+1]));
			for(int i=0; i<cols.length; i++)
				resdataset.addColumn(new NumericalColumn("sd-"+cols[i]));
			for(int i=0; i<nObjectives; i++)
				resdataset.addColumn(new NumericalColumn("sd-"+objs[i+1]));

			// Combination of objectives

			for(File f1: objDir){

				System.out.println("f: " + f1.getName());

				if(f1.getName().contains("pareto")){
					aux = f1.getName().split("-");

					name2 = f1.getAbsolutePath().replace("pareto", "population");

					// Open dataset (pareto)
					dataset1 = new ExcelDataset(f1.getAbsolutePath());
					dataset1.setMissingValue("?");
					dataset1.setNullValue("#NUM!");

					try {
						dataset1.readDataset("nv", format1);
						exist1=true;
					} catch (Exception e) {
						exist1=false;
					}

					dataset2 = new ExcelDataset(name2);
					dataset2.setMissingValue("?");
					dataset2.setNullValue("#NUM!");

					try {
						dataset2.readDataset("nv", format2);
						exist2=true;
					} catch (Exception  e) {
						exist2=false;
					}

					if(exist1 && exist2){

						if (dataset1.getColumn(0).getSize() == 32){ // check size (30 executions)
							// Save mean and stdev results
							name = aux[0] + "-" + aux[1];
							resdataset.getColumn(0).addValue(name);	// algorithm + problem instance

							try{
								// Mean values
								resdataset.getColumn(1).addValue(dataset1.getColumn(1).getElement(30)); // #pareto
								resdataset.getColumn(2).addValue(dataset1.getColumn(3).getElement(30)); // time
								resdataset.getColumn(3).addValue(dataset1.getColumn(4).getElement(30)); // invalids
								resdataset.getColumn(4).addValue(dataset1.getColumn(2).getElement(30)); // components
								resdataset.getColumn(5).addValue(dataset2.getColumn(3).getElement(30)); // connectors
								// objectives
								int col = 6;
								for(int i=0; i<nObjectives; i++){
									resdataset.getColumn(col).addValue(dataset1.getColumnByName(objs[i+1]).getElement(30));
									col++;
								}
								// Standard deviation values
								resdataset.getColumn(col).addValue(dataset1.getColumn(1).getElement(31)); // #pareto
								resdataset.getColumn(col+1).addValue(dataset1.getColumn(3).getElement(31)); // time
								resdataset.getColumn(col+2).addValue(dataset1.getColumn(4).getElement(31)); // invalids
								resdataset.getColumn(col+3).addValue(dataset1.getColumn(2).getElement(31)); // components
								resdataset.getColumn(col+4).addValue(dataset2.getColumn(3).getElement(31)); // connectors
								// objectives
								col+=5;
								for(int i=0; i<nObjectives; i++){
									resdataset.getColumn(col).addValue(dataset1.getColumnByName(objs[i+1]).getElement(31));
									col++;
								}

							}catch(Exception e){
								e.printStackTrace();
								System.out.println("Problema con valores en: " + dataset1.getFileName());
							}
						}
						else{
							System.out.println("Faltan ejecuciones en: " + dataset1.getFileName());
						}
					}
				}
			}


			// Save dataset

			resdataset.setMissingValue("-");
			resdataset.setNullValue("-");
			try {
				System.out.println("Salida: <" + resdirname + resname+">");
				((ExcelDataset)resdataset).writeDataset(resdirname + resname);
			} catch (IOException e) {
				System.out.println("Problems saving the results dataset: " + resname);
				e.printStackTrace();
			}
		}
	}
}