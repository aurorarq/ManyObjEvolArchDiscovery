package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import es.uco.kdis.datapro.dataset.InstanceIterator;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.datatypes.InvalidValue;

public class DirectoryTests2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//String dirname1 = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-indicadores\\8obj";
		//String dirname2 = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\tests\\8obj";

		String dirname1 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\9obj";
		String dirname2 = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\tests\\9obj";

		
		File dir1 = new File(dirname1);
		File dir2 = new File(dirname2);
		ExcelDataset dataset;

		if(!dir2.exists()){
			dir2.mkdirs();
		}

		//File objs [] = dir1.listFiles();
		File files [];
		InstanceIterator iterator;
		List<Object> instance;
		String alg, problem;
		double hv, s;
		String [] aux;
		FileWriter writerHv, writerS;
		File testFile1, testFile2;
		ArrayList<String> problems = new ArrayList<String>();

		try {

			//for(File o: objs){
				//System.out.println(o.getAbsolutePath());

				//files = o.listFiles();
				files = dir1.listFiles();
				//File dir3 = new File(dirname2+"/"+o.getName());
				//if(!dir3.exists())
				//	dir3.mkdirs();
				
				for(File f: files){
					
					String [] aux2 = f.getName().split("-");
					String combi = "";
					for(int i=0; i<aux2.length-3; i++)
						combi += aux2[i]+"-";
					combi += aux2[aux2.length-3];
					File dir3 = new File(dirname2+"/"+combi);
					if(!dir3.exists())
						dir3.mkdirs();
					
					if(f.isFile()){

						problems.clear();

						System.out.println(f.getName());
						dataset = new ExcelDataset(f.getAbsolutePath());
						dataset.setNullValue("#NUM!");

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
							
							testFile1 = new File(dirname2+"/"+combi+"/"+problem+"-h.txt");
							testFile2 = new File(dirname2+"/"+combi+"/"+problem+"-s.txt");


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
						writerHv = new FileWriter(dirname2+"/" +combi+ "/ficheros-h.txt");
						writerS = new FileWriter(dirname2+"/" + combi+"/ficheros-s.txt");

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
