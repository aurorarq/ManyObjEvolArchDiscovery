package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import es.uco.kdis.datapro.dataset.InstanceIterator;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.datatypes.InvalidValue;

public class DirectoryTests3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//String dirname1 = "C:\\Users\\Aurora\\Desktop\\icd-erp-gcr-ins-cs-en-cl-abs";
		//String dirname2 = "C:\\Users\\Aurora\\Desktop\\tests-8";

		String dirname1 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\9obj\\icd-erp-gcr-ins-cs-en-cl-cb-abs";
		String dirname2 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\tests\\9obj+nsga3\\icd-erp-gcr-ins-cs-en-cl-cb-abs";
		
		File dir1 = new File(dirname1);
		File dir2 = new File(dirname2);
		ExcelDataset dataset;

		if(!dir2.exists()){
			dir2.mkdirs();
		}

		File files [];
		InstanceIterator iterator;
		List<Object> instance;
		String alg, problem;
		double hv, s;
		String [] aux;
		FileWriter writerHv, writerS;
		File testFile1, testFile2;
		ArrayList<String> problems = new ArrayList<String>();

		String combi = dir1.getName();
		try {
				files = dir1.listFiles();
				
				for(File f: files){
					
					//if(f.isFile()){
					if(f.isFile() && f.getName().contains("2")){
						problems.clear();

						System.out.println(f.getName());
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

							//testFile1 = new File(dirname2+"/"+o.getName()+"/"+problem+"-h.txt");
							//testFile2 = new File(dirname2+"/"+o.getName()+"/"+problem+"-s.txt");
							
							testFile1 = new File(dirname2+"/"+"/"+problem+"-h.txt");
							testFile2 = new File(dirname2+"/"+"/"+problem+"-s.txt");


							writerHv = new FileWriter(testFile1,true);
							writerS = new FileWriter(testFile2,true);

							writerHv.append(alg+"\t"+hv+"\n");
							writerS.append(alg+"\t"+s+"\n");

							writerHv.close();
							writerS.close();
						}

						// Fichero resumen
						//writerHv = new FileWriter(dirname2+"/" + o.getName()+ "/ficheros-h.txt");
						//writerS = new FileWriter(dirname2+"/" + o.getName()+"/ficheros-s.txt");
						writerHv = new FileWriter(dirname2+"/" + "/ficheros-h.txt");
						writerS = new FileWriter(dirname2+"/" +"/ficheros-s.txt");

						for(String p: problems){
							//writerHv.append(o.getName()+"/"+p+"-h max\n");
							//writerS.append(o.getName()+"/"+p+"-s max\n");
							writerHv.append(combi+"/"+p+"-h max\n");
							writerS.append(combi+"/"+p+"-s max\n");
						}

						writerHv.close();
						writerS.close();
					}
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
