package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.CsvDataset;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.datatypes.InvalidValue;
import es.uco.kdis.datapro.datatypes.NullValue;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class NormalizeIndicators2 {

	protected String dirname;
	protected String resFrontsDirname = "/res-fronts/";
	//protected String resNormDirname = "/res-fronts-norm/";
	protected String resNormDirname = "/res-fronts-norm-2/";
	protected int numOfObjectives;

	public NormalizeIndicators2(String dirname, int nObjs){
		this.dirname = dirname;
		this.numOfObjectives = nObjs;
	}

	public static void main(String[] args) {

		//NormalizeIndicators2 obj = new NormalizeIndicators2(args[0], Integer.parseInt(args[1]));
		String dirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\6obj-4\\erp-gcr-cs-en-cl-cb";
		int objs = 6;
		NormalizeIndicators2 obj = new NormalizeIndicators2(dirname,objs);
		obj.normalize();
		obj.indicators();
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
		//File [] objDirs;
		File [] probDirs;
		File [] algDirs;
		File resFrontsDir;
		File resFrontsNorDir;
		File [] files;
		boolean empty = false;

		dir1 = new File(dirname);

		if(dir1.exists()){

			// Directory with different combinations of objectives
			//objDirs = dir1.listFiles();

			//for (File d1: objDirs){

			// Directory with different problem instances
			probDirs = dir1.listFiles();

			for(File p: probDirs){

				/*if(p.isFile())
					p.delete();*/
				//else{
				if(p.isDirectory()){
					// Directory with fronts
					resFrontsDir = new File(p.getAbsolutePath()+this.resFrontsDirname);

					// Classified by Algorithms
					algDirs = resFrontsDir.listFiles();

					// Create the directory of results
					resFrontsNorDir = new File(p.getAbsolutePath()+this.resNormDirname);
					if(resFrontsNorDir.exists()){
						File [] f2 = resFrontsNorDir.listFiles();
						for(File f: f2){
							f.delete();
						}
					}
					else
						resFrontsNorDir.mkdirs();

					// Clean values
					for(int i=0; i<numOfObjectives; i++){
						min[i] = Double.POSITIVE_INFINITY;
						max[i] = Double.NEGATIVE_INFINITY;
					}

					for(File a: algDirs){

						// Is a directory
						//if(a.isDirectory() && !a.getName().contains("NSGA3")){
						if(a.isDirectory()){

							// Open each dataset containing a front and compute the
							// minimum and maximum values of each objective
							files = a.listFiles();

							for(File f: files){
								//System.out.println(f.getAbsolutePath());
								// Open dataset
								//dataset = new ExcelDataset(f.getAbsolutePath());
								dataset = new CsvDataset(f.getAbsolutePath());
								empty=false;
								try {
									//((ExcelDataset)dataset).readDataset("nv", format);
									((CsvDataset)dataset).readDataset("nv", format);
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
					}

					// Now, normalize the values of each solution in each front
					// and saved it into the normalized fronts directory
					for(File a: algDirs){

						// Is a directory
						if(a.isDirectory()){
						//if(a.isDirectory()&&!a.getName().contains("NSGA3")){

							files = a.listFiles();
							for(File f: files){
								// Open dataset
								//dataset = new ExcelDataset(f.getAbsolutePath());
								dataset = new CsvDataset(f.getAbsolutePath());
								empty=false;
								try {
									//((ExcelDataset)dataset).readDataset("nv", format);
									((CsvDataset)dataset).readDataset("nv", format);
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
								String name = "norm-" + f.getName();
								//String dirname2 = this.dirname + "/" + d1.getName() + "/" + p.getName() + "/" + this.resNormDirname + "/" + a.getName();
								String dirname2 = this.dirname + "/" + p.getName() + "/" + this.resNormDirname + "/" + a.getName();
								File normDir = new File(dirname2);
								if(!normDir.exists()){
									normDir.mkdirs();
								}

								try {
									//((ExcelDataset)dataset).writeDataset(normDir + "/" + name);
									((CsvDataset)dataset).writeDataset(normDir + "/" + name);
								} catch (IOException e) {
									e.printStackTrace();
									System.exit(-1);
								}

							}

						}
					}
				}
			}
			//}
		}
	}

	public void indicators(){
		//File [] objDirs;
		File [] probDirs;
		File [] algDirs;
		File [] fronts;
		String auxdirname;
		File auxdir;

		Hypervolume hypervolume = new Hypervolume();
		SpacingMatrix spacing = new SpacingMatrix();
		double hvalue, svalue;
		double [][] front;
		Dataset dataset, res = null, res2;
		NumericalColumn col;

		String format = "";
		for(int i=0; i<numOfObjectives; i++)
			format+="f";

		File dir = new File(this.dirname);

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
					auxdirname = p.getAbsolutePath() + this.resNormDirname;
					auxdir = new File(auxdirname);
					algDirs = auxdir.listFiles();

					// For each algorithm
					for(File a: algDirs){

						System.out.println("\tAlgoritmo: " + a.getName());
						//if(a.isDirectory() && !a.getName().contains("NSGA3")){
						if(a.isDirectory()){
							fronts = a.listFiles();

							// Create a dataset for the results of each execution
							res = new ExcelDataset();
							res.addColumn(new NominalColumn("Algorithm"));	// The algorithm name
							res.addColumn(new NumericalColumn("Hypervolume")); // Hypervolume
							res.addColumn(new NumericalColumn("Spacing"));	// Spacing

							for(File f: fronts){
								// Open dataset
								//dataset = new ExcelDataset(f.getAbsolutePath());
								//dataset.setMissingValue("#NUM!");
								
								dataset = new CsvDataset(f.getAbsolutePath());
								dataset.setMissingValue("?");
								
								try {
									//((ExcelDataset)dataset).readDataset("nv", format);
									((CsvDataset)dataset).readDataset("nv", format);
								} catch (IndexOutOfBoundsException | IOException
										| NotAddedValueException
										| IllegalFormatSpecificationException e) {
									e.printStackTrace();
									System.exit(-1);
								}
								catch(InputMismatchException e){
									// Do nothing
								}

								// Create a matrix with the column values
								int numOfInds = dataset.getColumn(0).getSize();
								if(numOfInds>0){
									front = new double[numOfInds][numOfObjectives];
									for(int j=0; j<numOfObjectives; j++){
										col = (NumericalColumn)dataset.getColumn(j);
										for(int i=0; i<numOfInds; i++){
											if(col.getElement(i) instanceof InvalidValue)
												front[i][j] = 0.0;
											else
												front[i][j] = ((Double)col.getElement(i)).doubleValue();
										}
									}

									// Calculate each indicator
									hvalue = hypervolume.calculateHypervolume(front, numOfInds, numOfObjectives);
									svalue = spacing.calculateSpacing(front, numOfInds);

									// Save in the result dataset
									res.getColumn(1).addValue(hvalue);
									res.getColumn(2).addValue(svalue);
								}
								// Indicators can not be computed
								else{
									res.getColumn(1).addValue(NullValue.getNullValue());
									res.getColumn(2).addValue(NullValue.getNullValue());
								}
								res.getColumn(0).addValue(f.getName());
							}

							// Compute mean and standard deviation
							double avg1 = ((NumericalColumn)res.getColumn(1)).mean();
							double sd1 = ((NumericalColumn)res.getColumn(1)).standardDeviation();
							double avg2 = ((NumericalColumn)res.getColumn(2)).mean();
							double sd2 = ((NumericalColumn)res.getColumn(2)).standardDeviation();

							// Save results dataset
							res.getColumn(0).addValue("MEAN");
							res.getColumn(0).addValue("SD");
							res.getColumn(1).addValue(avg1);
							res.getColumn(1).addValue(sd1);
							res.getColumn(2).addValue(avg2);
							res.getColumn(2).addValue(sd2);

							try {
								((ExcelDataset)res).writeDataset(a + "-emo-indicators.xlsx");
							} catch (IOException e) {
								e.printStackTrace();
							}

							// Copy mean and standard deviation values in the second dataset
							res2.getColumn(0).addValue(p.getName()+"-"+a.getName());
							res2.getColumn(1).addValue(avg1);
							res2.getColumn(2).addValue(sd1);
							res2.getColumn(3).addValue(avg2);
							res2.getColumn(4).addValue(sd2);
						}
					}// end for algDirs

					// Save res2 dataset
					try {
						String path = dir.getAbsolutePath()+"/";
						//String name = dir.getName()+"-emo-indicators.xlsx";
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
