package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.Column.ColumnAbstraction;
import es.uco.kdis.datapro.dataset.Source.CsvDataset;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class ConvertCSV {

	protected String dirname;
	protected String resFrontsDirname = "/res-fronts/";
	protected int numOfObjectives;

	public ConvertCSV(String dirname, int nObjs){
		this.dirname = dirname;
		this.numOfObjectives = nObjs;
	}

	public static void main(String[] args) {
		String dirname = //"C:\\Users\\Aurora\\Desktop\\prueba";
				"G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\6obj-4";
		int objs = 6;
		ConvertCSV obj = new ConvertCSV(dirname,objs);
		obj.convert();
	}


	public void convert(){
		File dir1;
		Dataset dataset, dataset2;
		//boolean empty;
		String format = "";
		for(int i=0; i<numOfObjectives; i++)
			format+="f";

		// Get the minimum and maximum value for each objective
		// from all the non-dominated solutions found by all
		// the algorithms
		File [] objDirs;
		File [] probDirs;
		File [] algDirs;
		File resFrontsDir;	
		File [] files;
		dir1 = new File(dirname);
		String name;

		if(dir1.exists()){

			// Directory with different combinations of objectives
			objDirs = dir1.listFiles();

			for (File d1: objDirs){

				System.out.println(d1.getName());

				// Directory with different problem instances
				probDirs = d1.listFiles();

				for(File p: probDirs){	

					/*if(p.isFile())
						p.delete();
					else*/
					if(p.isDirectory()){

						System.out.println("\t"+p.getName());

						// Directory with fronts
						resFrontsDir = new File(p.getAbsolutePath()+this.resFrontsDirname);

						// Classified by Algorithms
						algDirs = resFrontsDir.listFiles();

						for(File a: algDirs){

							//System.out.println("\t\t"+a.getName());

							// Is a directory
							//if(a.isDirectory() && a.getName().contains("NSGA3")){
								if(a.isDirectory()){

								// Open each dataset containing a front and compute the
								// minimum and maximum values of each objective
								files = a.listFiles();

								for(File f: files){

									//System.out.println("\t\t\t"+f.getName());
									if(f.getAbsolutePath().contains(".xlsx")){
										// Open dataset
										dataset = new ExcelDataset(f.getAbsolutePath());
										//empty=false;
										try {
											((ExcelDataset)dataset).setNullValue("#NUM!");
											((ExcelDataset)dataset).readDataset("nv", format);
										} catch (IndexOutOfBoundsException | IOException
												| NotAddedValueException
												| IllegalFormatSpecificationException e) {
											e.printStackTrace();
											System.exit(-1);
										}
										catch(InputMismatchException e){
											//empty=true;
										}

										// Copy
										dataset2 = new CsvDataset();
										((CsvDataset)dataset2).setNullValue("?");
										((CsvDataset)dataset2).setNumberOfDecimals(15);
										for(ColumnAbstraction c: dataset.getColumns()){
											dataset2.addColumn(c);
										}

										// Write the new dataset
										name = f.getAbsolutePath();
										name = name.substring(0,name.indexOf("."));

										try {
											((CsvDataset)dataset2).writeDataset(name+".csv");
										} catch (IOException e) {
											e.printStackTrace();
										}

										// Remove the file
										f.delete();
									}
								}
							}
						}
					}
				}
			}
		}
	}
}