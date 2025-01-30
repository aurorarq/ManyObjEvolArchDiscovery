package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.CsvDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class ResultsDependenciesObjectives {

	double [][] measure1_min = new double [8][10];
	double [][] measure1_max = new double [8][10];
	double [][] measure2_min = new double [8][10];
	double [][] measure2_max = new double [8][10];

	double measure1_gmin;
	double measure1_gmax;
	double measure2_gmin;
	double measure2_gmax;

	String [] algorithms = new String [] {"MOEA/D", "GrEA", "HypE", "IBEA", "NSGA2", "NSGA3", "SPEA2", "eMOEA"};
	String [] instances = new String []	{"aqualush", "borg", "datapro4j", "ical4j", "java2html", "jsapar", "jxls", "logisim", "marvin", "nekohtml"};

	//String [] algorithms = new String [] {"MOEAD", "NSGA2"};
	//String [] instances = new String []	{"aqualush", "datapro"};


	public static void main(String[] args) {

		//String dirname = "C:\\Users\\Aurora\\Desktop\\2obj";
		String dirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\2obj";
		String resdirname = "C:\\Users\\Aurora\\Desktop\\res2";
		int objs = 2;
		ResultsDependenciesObjectives obj = new ResultsDependenciesObjectives();

		File dir = new File(dirname);
		File [] combinations;
		File resdir = new File(resdirname);
		String resfilename;
		String [] aux;

		if(!resdir.exists()){
			resdir.mkdirs();
		}

		if(dir.isDirectory()){
			combinations = dir.listFiles();

			for(File c: combinations){

				if(c.isDirectory() && c.getName().equalsIgnoreCase("cl-abs")){
					aux = c.getName().split("-");

					obj.cleanValues();
					
					// Get min and max values from all the fronts
					//obj.extractSolutionsRange(c,objs);

					// Extract non-dominated solutions for each algorithm and problem instance
					obj.extractNonDominatedSolutionsAlgorithm(c, objs, aux[0], aux[1]);

					// Extract non-dominated solutions for each problem instance
					obj.extractNonDominatedSolutionsInstance(c, objs, aux[0], aux[1]);

					// Get the range of the obtained PF
					//obj.extractSolutionsRange(c, objs);
					obj.extractNonDominatedSolutionsRange(c, objs);
					
					// Global results
					obj.calculateOverallBounds();
					
					// Save results
					resfilename = resdirname + "/" + c.getName() + ".txt";
					//obj.saveResults(resfilename, aux[0], aux[1]);	
					obj.saveResultsNonDominated(resfilename, aux[0], aux[1]);		
					
				}
			}
		}
	}

	public void cleanValues(){
		measure1_min = null;
		measure1_max = null;
		measure2_min = null;
		measure2_max = null;
		measure1_gmin = Double.POSITIVE_INFINITY;
		measure1_gmax = Double.NEGATIVE_INFINITY;
		measure2_gmin = Double.POSITIVE_INFINITY;
		measure2_gmax = Double.NEGATIVE_INFINITY;
	}

	public void extractSolutionsRange(File directory, int numOfObjectives){
		measure1_min = new double [8][10];
		measure1_max = new double [8][10];
		measure2_min = new double [8][10];
		measure2_max = new double [8][10];

		File [] instancesDir = directory.listFiles();
		File [] algorithmsDir;
		String frontsDirectory;
		File [] fronts;
		Dataset dataset;
		boolean empty;
		NumericalColumn column;
		double minCol1, maxCol1, minCol2, maxCol2;

		String format = "";
		for(int i=0; i<numOfObjectives; i++)
			format+="f";

		int col = -1, row;
		// Each problem instance
		for(File iDir: instancesDir){
			if(iDir.isDirectory()){
				col++;
				row = -1;
				frontsDirectory = iDir.getAbsolutePath()+"\\res-fronts\\";
				algorithmsDir = (new File(frontsDirectory)).listFiles();

				// Each algorithm
				for(File aDir: algorithmsDir){
					if(aDir.isDirectory()){
						row++;

						measure1_min[row][col] = Double.POSITIVE_INFINITY;
						measure2_min[row][col] = Double.POSITIVE_INFINITY;
						measure1_max[row][col] = Double.NEGATIVE_INFINITY;
						measure2_max[row][col] = Double.NEGATIVE_INFINITY;

						// Open all the fronts
						fronts = aDir.listFiles();

						for(File f: fronts){
							dataset = new CsvDataset(f.getAbsolutePath());
							empty=false;
							try {
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

							// Minimum and maximum for this algorithm and problem instance
							if(!empty){

								column = (NumericalColumn)dataset.getColumn(0);
								minCol1 = column.getMinValue();
								maxCol1 = column.getMaxValue();

								column = (NumericalColumn)dataset.getColumn(1);
								minCol2 = column.getMinValue();
								maxCol2= column.getMaxValue();

								if(minCol1 < measure1_min[row][col])
									measure1_min[row][col] = minCol1;

								if(maxCol1 > measure1_max[row][col])
									measure1_max[row][col] = maxCol1;

								if(minCol2 < measure2_min[row][col])
									measure2_min[row][col] = minCol2;

								if(maxCol2 > measure2_max[row][col])
									measure2_max[row][col] = maxCol2;
							}
						}
					}
				}
			}
		}
	}

	public void extractNonDominatedSolutionsAlgorithm(File directory, int numOfObjectives, String measure1, String measure2){
		File [] instancesDir = directory.listFiles();
		File [] algorithmsDir;
		String frontsDirectory;
		File [] fronts;
		Dataset dataset, dataset2;
		boolean empty;
		Dataset nonDominatedDataset;
		double [] solution1 = new double[numOfObjectives];
		double [] solution2 = new double[numOfObjectives];
		int nSolutions1, nSolutions2;
		boolean isDominated;
		String format = "";
		for(int i=0; i<numOfObjectives; i++){
			format+="f";
		}

		// Each problem instance
		for(File iDir: instancesDir){
			if(iDir.isDirectory()){

				frontsDirectory = iDir.getAbsolutePath()+"\\res-fronts\\";
				algorithmsDir = (new File(frontsDirectory)).listFiles();

				// Each algorithm
				for(File aDir: algorithmsDir){
					if(aDir.isDirectory()){

						// CREATE THE DATASET
						nonDominatedDataset = new CsvDataset();
						for(int i=0; i<numOfObjectives; i++){
							nonDominatedDataset.addColumn(new NumericalColumn());
						}
						nonDominatedDataset.getColumn(0).setName(measure1);
						nonDominatedDataset.getColumn(1).setName(measure2);

						// Open all the fronts
						fronts = aDir.listFiles();

						for(int f1 = 0; f1<fronts.length; f1++){

							dataset = new CsvDataset(fronts[f1].getAbsolutePath());
							empty=false;
							try {
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

							// Minimum and maximum for this algorithm and problem instance
							if(!empty){

								// Extract each instance
								nSolutions1 = dataset.getColumn(0).getSize();
								for(int i=0; i<nSolutions1; i++){
									for(int j=0; j<numOfObjectives; j++){
										solution1[j] = (double) dataset.getColumn(j).getElement(i);
									}
									isDominated = false;

									// Compare with the rest of instances of the same algorithm and problem instance
									for(int f2=0; !isDominated && f2<fronts.length; f2++){
										if(f1!=f2){
											dataset2 = new CsvDataset(fronts[f2].getAbsolutePath());
											empty=false;
											try {
												((CsvDataset)dataset2).readDataset("nv", format);
											} catch (IndexOutOfBoundsException | IOException
													| NotAddedValueException
													| IllegalFormatSpecificationException e) {
												e.printStackTrace();
												System.exit(-1);
											}
											catch(InputMismatchException e){
												empty=true;
											}

											// Minimum and maximum for this algorithm and problem instance
											if(!empty){

												// Extract each instance
												nSolutions2 = dataset2.getColumn(0).getSize();
												for(int k=0; k<nSolutions2; k++){
													for(int l=0; l<numOfObjectives; l++){
														solution2[l] = (double) dataset2.getColumn(l).getElement(k);
													}

													// solution 1 dominates solution 2 ?
													if(dominates(solution2, solution1)){
														isDominated = true;
													}
												}
											}
										}
									}

									// If the solution is non-dominated, add to the front
									if(!isDominated){
										for(int n=0; n<solution1.length; n++){
											nonDominatedDataset.getColumn(n).addValue(solution1[n]);
										}
									}
								}

							}
						}


						// SAVE THE FRONT FOR THIS ALGORITHM
						try {
							((CsvDataset)nonDominatedDataset).writeDataset(iDir.getAbsolutePath() + "/" + aDir.getName() + "-nondominated.csv");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void extractNonDominatedSolutionsInstance(File directory, int numOfObjectives, String measure1, String measure2){
		File [] instancesDir = directory.listFiles();
		File [] algorithmsDir;
		String frontsDirectory;
		File algorithmFront, algorithm2Front;
		Dataset dataset, dataset2;
		boolean empty;
		Dataset nonDominatedDataset;
		double [] solution1 = new double[numOfObjectives];
		double [] solution2 = new double[numOfObjectives];
		int nSolutions1, nSolutions2;
		boolean isDominated;
		String format = "";
		for(int i=0; i<numOfObjectives; i++){
			format+="f";
		}

		// Each problem instance
		for(File iDir: instancesDir){
			if(iDir.isDirectory()){

				frontsDirectory = iDir.getAbsolutePath();
				algorithmsDir = (new File(frontsDirectory)).listFiles();

				// CREATE THE DATASET
				nonDominatedDataset = new CsvDataset();
				for(int i=0; i<numOfObjectives; i++){
					nonDominatedDataset.addColumn(new NumericalColumn());
				}
				nonDominatedDataset.addColumn(new NominalColumn("algorithm"));
				nonDominatedDataset.getColumn(0).setName(measure1);
				nonDominatedDataset.getColumn(1).setName(measure2);

				// Each algorithm
				for(int m=0; m<algorithmsDir.length; m++){
					algorithmFront = algorithmsDir[m];

					if(algorithmFront.isFile()){

						// Open the front
						dataset = new CsvDataset(algorithmFront.getAbsolutePath());
						empty=false;
						try {
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

						// Minimum and maximum for this algorithm and problem instance
						if(!empty){

							// Extract each instance
							nSolutions1 = dataset.getColumn(0).getSize();
							for(int i=0; i<nSolutions1; i++){
								for(int j=0; j<numOfObjectives; j++){
									solution1[j] = (double) dataset.getColumn(j).getElement(i);
								}
								isDominated = false;

								// Compare with the rest of instances of the same algorithm and problem instance
								for(int f2=0; !isDominated && f2<algorithmsDir.length; f2++){
									algorithm2Front = algorithmsDir[f2];
									if(algorithm2Front.isFile() && m!=f2){
										dataset2 = new CsvDataset(algorithm2Front.getAbsolutePath());
										empty=false;
										try {
											((CsvDataset)dataset2).readDataset("nv", format);
										} catch (IndexOutOfBoundsException | IOException
												| NotAddedValueException
												| IllegalFormatSpecificationException e) {
											e.printStackTrace();
											System.exit(-1);
										}
										catch(InputMismatchException e){
											empty=true;
										}

										if(!empty){

											// Extract each instance
											nSolutions2 = dataset2.getColumn(0).getSize();
											for(int k=0; k<nSolutions2; k++){
												for(int l=0; l<numOfObjectives; l++){
													solution2[l] = (double) dataset2.getColumn(l).getElement(k);
												}

												// solution 1 dominates solution 2 ?
												if(dominates(solution2, solution1)){
													isDominated = true;
												}
											}
										}
									}
								}

								// If the solution is non-dominated, add to the front
								if(!isDominated){
									for(int n=0; n<solution1.length; n++){
										nonDominatedDataset.getColumn(n).addValue(solution1[n]);
									}
									nonDominatedDataset.getColumn(numOfObjectives).addValue(algorithmFront.getName().substring(0, algorithmFront.getName().indexOf("-")));
								}
							}
						}
					}
				}

				// SAVE THE FRONT FOR THIS PROBLEM INSTANCE
				try {
					((CsvDataset)nonDominatedDataset).writeDataset(directory.getAbsolutePath() + "/" + iDir.getName() + "-nondominated.csv");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void extractNonDominatedSolutionsRange(File directory, int numOfObjectives){
		measure1_min = new double [1][10];
		measure1_max = new double [1][10];
		measure2_min = new double [1][10];
		measure2_max = new double [1][10];

		File [] files = directory.listFiles();
		Dataset dataset;
		boolean empty;
		NumericalColumn column;
		String format = "";
		for(int i=0; i<numOfObjectives; i++)
			format+="f";
		format+="%"; // skip the last column (algorithm)
		
		int col = -1;
		// Each problem instance
		for(File f: files){
			
			if(f.isFile() && f.getName().contains("nondominated")){
				col++;	
				
				// Open the front
				dataset = new CsvDataset(f.getAbsolutePath());
				empty=false;
				try {
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

				// Minimum and maximum for this problem instance
				if(!empty){
					
					column = (NumericalColumn)dataset.getColumn(0);
					measure1_min[0][col] = column.getMinValue();
					measure1_max[0][col] = column.getMaxValue();
					
					column = (NumericalColumn)dataset.getColumn(1);
					measure2_min[0][col] = column.getMinValue();
					measure2_max[0][col] = column.getMaxValue();
				}
			}
		}
	}

	public void calculateOverallBounds(){

		// Global minimum of measure1
		for(int i=0; i<measure1_min.length; i++){
			for(int j=0; j<measure1_min[0].length; j++){
				if(measure1_min[i][j] < measure1_gmin){
					measure1_gmin = measure1_min[i][j];
				}
			}
		}

		// Global maximum of measure1
		for(int i=0; i<measure1_max.length; i++){
			for(int j=0; j<measure1_max[0].length; j++){
				if(measure1_max[i][j] > measure1_gmax){
					measure1_gmax = measure1_max[i][j];
				}
			}
		}

		// Global minimum of measure2
		for(int i=0; i<measure2_min.length; i++){
			for(int j=0; j<measure2_min[0].length; j++){
				if(measure2_min[i][j] < measure2_gmin){
					measure2_gmin = measure2_min[i][j];
				}
			}
		}

		// Global maximum of measure2
		for(int i=0; i<measure2_max.length; i++){
			for(int j=0; j<measure2_max[0].length; j++){
				if(measure2_max[i][j] > measure2_gmax){
					measure2_gmax = measure2_max[i][j];
				}
			}
		}
	}

	public void saveResults(String resdirname, String measure1, String measure2){
		File file = new File(resdirname);
		StringBuffer buffer = new StringBuffer();

		// GLOBAL RESULTS
		buffer.append("GLOBAL RESULTS\n");
		buffer.append("Measure1: name=" + measure1 + " min=" + measure1_gmin + " max=" + measure1_gmax + "\n");
		buffer.append("Measure2: name=" + measure2 + " min=" + measure2_gmin + " max=" + measure2_gmax + "\n");

		// MIN VALUES FOR MEASURE 1
		buffer.append("\nMIN VALUES FOR MEASURE1\n\t");

		for(int i=0; i<instances.length; i++){
			buffer.append("\t" + instances[i]);
		}

		for(int i=0; i<algorithms.length; i++){
			buffer.append("\n"+algorithms[i]);
			for(int j=0; j<instances.length; j++){
				buffer.append("\t" + measure1_min[i][j]);
			}
		}

		// MAX VALUES FOR MEASURE 1
		buffer.append("\n\nMAX VALUES FOR MEASURE1\n\t");
		for(int i=0; i<instances.length; i++){
			buffer.append("\t" + instances[i]);
		}

		for(int i=0; i<algorithms.length; i++){
			buffer.append("\n"+algorithms[i]);
			for(int j=0; j<instances.length; j++){
				buffer.append("\t" + measure1_max[i][j]);
			}
		}

		// MIN VALUES FOR MEASURE 2
		buffer.append("\n\nMIN VALUES FOR MEASURE2\n\t");
		for(int i=0; i<instances.length; i++){
			buffer.append("\t" + instances[i]);
		}

		for(int i=0; i<algorithms.length; i++){
			buffer.append("\n"+algorithms[i]);
			for(int j=0; j<instances.length; j++){
				buffer.append("\t" + measure2_min[i][j]);
			}
		}

		// MAX VALUES FOR MEASURE 2
		buffer.append("\n\nMAX VALUES FOR MEASURE2\n\t");
		for(int i=0; i<instances.length; i++){
			buffer.append("\t" + instances[i]);
		}

		for(int i=0; i<algorithms.length; i++){
			buffer.append("\n"+algorithms[i]);
			for(int j=0; j<instances.length; j++){
				buffer.append("\t" + measure2_max[i][j]);
			}
		}

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(buffer.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveResultsNonDominated(String resdirname, String measure1, String measure2){
		File file = new File(resdirname);
		StringBuffer buffer = new StringBuffer();

		// GLOBAL RESULTS
		buffer.append("GLOBAL RESULTS\n");
		buffer.append("Measure1: name=" + measure1 + " min=" + measure1_gmin + " max=" + measure1_gmax + "\n");
		buffer.append("Measure2: name=" + measure2 + " min=" + measure2_gmin + " max=" + measure2_gmax + "\n");

		// MIN VALUES FOR MEASURE 1
		buffer.append("\nMIN VALUES FOR MEASURE1\n");

		for(int j=0; j<instances.length; j++){
			buffer.append(instances[j] + ": " + measure1_min[0][j] + "\n");
		}

		// MAX VALUES FOR MEASURE 1
		buffer.append("\n\nMAX VALUES FOR MEASURE1\n");
		for(int j=0; j<instances.length; j++){
			buffer.append(instances[j] + ": " + measure1_max[0][j] + "\n");
		}

		// MIN VALUES FOR MEASURE 2
		buffer.append("\n\nMIN VALUES FOR MEASURE2\n");
		for(int j=0; j<instances.length; j++){
			buffer.append(instances[j] + ": " + measure2_min[0][j] + "\n");
		}

		// MAX VALUES FOR MEASURE 2
		buffer.append("\n\nMAX VALUES FOR MEASURE2\n");
		for(int j=0; j<instances.length; j++){
			buffer.append(instances[j] + ": " + measure2_max[0][j] + "\n");
		}

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(buffer.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean dominates(double [] solution1, double [] solution2){
		int betterInAnyObjective = 0, i;
		int nObjectives = solution1.length;
		for (i=0; i<nObjectives && solution1[i]>=solution2[i]; i++){
			if (solution1[i] > solution2[i]) {
				betterInAnyObjective = 1;
			}
		}
		return ((i >= nObjectives) && (betterInAnyObjective>0));
	}
}
