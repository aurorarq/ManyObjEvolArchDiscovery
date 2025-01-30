package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;

public class SummarizeIndicators {

	public static void main(String[] args) {

		String dirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\9obj\\icd-erp-gcr-ins-cs-en-cl-cb-abs";
		String resdirname = "res-fronts-norm-2";
		//File [] objDirs;
		File [] probDirs;
		File [] algDirs;
		String auxdirname;
		File auxdir;

		Dataset dataset, res2;

		File dir = new File(dirname);

		System.out.println(dir.getAbsolutePath());

		if(dir.exists()){
			//objDirs = dir.listFiles();

			// For each combinations of objectives
			//for(File o: objDirs){

			// Create a dataset for the mean results of each algorithm
			res2 = new ExcelDataset();
			res2.addColumn(new NominalColumn("Algorithm"));	// The algorithm name
			res2.addColumn(new NumericalColumn("H-AVG")); // Hypervolume
			res2.addColumn(new NumericalColumn("H-SD")); // Hypervolume
			res2.addColumn(new NumericalColumn("S-AVG"));	// Spacing
			res2.addColumn(new NumericalColumn("S-SD"));	// Spacing

			//probDirs = o.listFiles();
			probDirs = dir.listFiles();

			// For each problem instance
			for(File p: probDirs){


				if(p.isDirectory()){
					System.out.println("\t"+p.getName());
					auxdirname = p.getAbsolutePath() + "/" + resdirname;
					auxdir = new File(auxdirname);
					algDirs = auxdir.listFiles();

					// For each algorithm
					for(File a: algDirs){

						System.out.println("\tAlgoritmo: " + a.getName());
						//if(a.isDirectory() && !a.getName().contains("NSGA3")){
						if(a.isFile()){
							dataset = new ExcelDataset(a.getAbsolutePath());
							dataset.setMissingValue("#NUM!");
							try {
								((ExcelDataset)dataset).readDataset("nv", "sff");
							} catch (Exception e) {
								e.printStackTrace();
								System.exit(-1);
							}
							
							Object avg1 = dataset.getColumn(1).getElement(dataset.getColumn(1).getSize()-2);
							Object sd1 = dataset.getColumn(1).getElement(dataset.getColumn(1).getSize()-1);
							Object avg2 = dataset.getColumn(2).getElement(dataset.getColumn(2).getSize()-2);
							Object sd2 = dataset.getColumn(2).getElement(dataset.getColumn(2).getSize()-1);

							// Copy mean and standard deviation values in the second dataset
							res2.getColumn(0).addValue(p.getName()+"-"+a.getName().substring(0,a.getName().indexOf("emo")));
							res2.getColumn(1).addValue(avg1);
							res2.getColumn(2).addValue(sd1);
							res2.getColumn(3).addValue(avg2);
							res2.getColumn(4).addValue(sd2);
						}
					}// end for algDirs

					// Save res2 dataset
					try {
						String path = dir.getAbsolutePath()+"/";
						String name = dir.getName()+"-emo-indicators2.xlsx";
						((ExcelDataset)res2).writeDataset(path+"/"+name);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}// end for probDirs

			//}// end for objDirs

		}

	}

}
