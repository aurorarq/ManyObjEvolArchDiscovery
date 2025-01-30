package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import es.uco.kdis.datapro.dataset.InstanceIterator;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.datatypes.InvalidValue;

public class DirectoryTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	//	String dirname1 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\congresos\\2015_MAEB\\experimentación\\rawdata\\8obj";
	//	String dirname2 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\congresos\\2015_MAEB\\experimentación\\tests\\8obj";

		String dirname1 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\6obj-1";
		String dirname2 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\tests\\6obj+nsga3";

		
		File dir1 = new File(dirname1);
		File dir2 = new File(dirname2);
		ExcelDataset dataset;

		if(!dir2.exists()){
			dir2.mkdirs();
		}

		File objs [] = dir1.listFiles();
		File files [];
		InstanceIterator iterator;
		List<Object> instance;
		String alg, problem;
		double hv, s;
		String [] aux;
		String [] aux2;
		FileWriter writerHv, writerS;
		File testFile1, testFile2;
		ArrayList<String> problems = new ArrayList<String>();
		String combi;
		File dir3;
		
		try {

			for(File o: objs){
				files = o.listFiles();
							
				for(File f: files){

					//if(f.isFile() && !f.getName().contains("2")){
					if(f.isFile() && f.getName().contains("2")){	
						aux2 = f.getName().split("-");
						combi = "";
						for(int i=0; i<aux2.length-3; i++)
							combi += aux2[i]+"-";
						combi += aux2[aux2.length-3];
						//System.out.println("combi: " + combi);
						
						dir3 = new File(dirname2+"/"+combi);
						if(!dir3.exists())
							dir3.mkdirs();
						
						problems.clear();

						dataset = new ExcelDataset(f.getAbsolutePath());
						dataset.setNullValue("#NUM!");
						//dataset.setNullValue("?");

						dataset.readDataset("nv", "sffff");

						iterator = new InstanceIterator(dataset);

						while(!iterator.isDone()){
							instance = iterator.currentInstance();
							iterator.next();

							aux = instance.get(0).toString().split("-");
							problem = aux[0];
							alg = aux[1];
							if(instance.get(1) instanceof InvalidValue)
								hv = -1;
							else
								hv = Double.parseDouble(instance.get(1).toString());
							
							if(instance.get(3) instanceof InvalidValue)
								s = -1;
							else
								s = Double.parseDouble(instance.get(3).toString());

												
							if(!problems.contains(problem))
								problems.add(problem);

							testFile1 = new File(dir3.getAbsolutePath()+"/"+problem+"-h.txt");
							testFile2 = new File(dir3.getAbsolutePath()+"/"+problem+"-s.txt");
							
							writerHv = new FileWriter(testFile1,true);
							writerS = new FileWriter(testFile2,true);

							writerHv.append(alg+"\t"+hv+"\n");
							writerS.append(alg+"\t"+s+"\n");
							
							//System.out.println("alg: " + alg + " hv: " + hv);
							//System.out.println("alg: " + alg + " s: " + hv);

							writerHv.close();
							writerS.close();
						}
						
						// Fichero resumen
						writerHv = new FileWriter(dir3.getAbsolutePath()+"/ficheros-h.txt");
						writerS = new FileWriter(dir3.getAbsolutePath()+"/ficheros-s.txt");
						
						for(String p: problems){
							writerHv.append(combi+"/"+p+"-h max\n");
							writerS.append(combi+"/"+p+"-s max\n");
						}

						writerHv.close();
						writerS.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
