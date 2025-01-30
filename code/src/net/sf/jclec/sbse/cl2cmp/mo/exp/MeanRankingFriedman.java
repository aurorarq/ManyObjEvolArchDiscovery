package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MeanRankingFriedman {

	/*
	 * 0 -> SPEA2
	 * 1 -> NSGA-II
	 * 2 -> MOEA/D
	 * 3 -> SSeMOEA
	 * 4 -> GrEA
	 * 5 -> IBEAe
	 * 6 -> HypE
	 * 7 -> NSGA-III
	 * */

	/*
	 * -1 -> italic
	 * 0 -> normal
	 * 1 -> bold
	 * 2 -> bold shadow
	 * */

	public static void main(String[] args) {

		String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-tests\\9obj+nsga3";
		File dir = new File(dirname);
		File [] files;
		StringBuffer sb = new StringBuffer();
		String [] algName = new String[]{"MOEA/D","GREA","HYPE","IBEA","NSGA2","NSGA3","SPEA2","EMOEA"};
		
		String line;
		String [] aux;
		int nAlgs = 8;
		int alg = -1;
		Double value;

		ArrayList<ArrayList<Double>> rankingsHV = new ArrayList<ArrayList<Double>>();
		for(int i=0; i<nAlgs; i++){
			rankingsHV.add(new ArrayList<Double>());
		}
		ArrayList<ArrayList<Double>> rankingsS = new ArrayList<ArrayList<Double>>();
		for(int i=0; i<nAlgs; i++){
			rankingsS.add(new ArrayList<Double>());
		}
		
		if(dir.isDirectory()){
			files = dir.listFiles();

			for(File f: files){

				if(f.getName().contains("-hv")){
				
					//////////////// HV
					alg=-1;
					try {

						BufferedReader reader = new BufferedReader(new FileReader(f));
						line = reader.readLine();

						while(!line.contains("<table border>")){
							line = reader.readLine();
						}
						line = reader.readLine();	// skip "</tr><td>Algorithm</td>"
						line = reader.readLine(); 	// skip "</tr>"

						while(!line.equalsIgnoreCase("</table>")){

							if(line.contains("<tr>")){
								alg++;
								aux = line.split("<td>");
								value = Double.valueOf(aux[2].substring(0, aux[2].indexOf("<")));
								rankingsHV.get(alg).add(value);	
							}
							line = reader.readLine();
						}
						reader.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//////////////// SPACING
					alg=-1;
					try {

						BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath().replace("-hv", "-s")));
						line = reader.readLine();

						while(!line.contains("<table border>")){
							line = reader.readLine();
						}
						line = reader.readLine();	// skip "</tr><td>Algorithm</td>"
						line = reader.readLine(); 	// skip "</tr>"

						while(!line.equalsIgnoreCase("</table>")){

							if(line.contains("<tr>")){
								alg++;
								aux = line.split("<td>");
								value = Double.valueOf(aux[2].substring(0, aux[2].indexOf("<")));
								rankingsS.get(alg).add(value);	
							}
							line = reader.readLine();
						}
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// CHECK
		int size = rankingsHV.get(0).size();
		System.out.println("HV SIZE: " + rankingsHV.get(0).size() + " S SIZE: " + rankingsS.get(0).size());
		
		//////////////////////////////////////// MEAN AND SD
		double meanHV, meanS, stdHV, stdS;
		for(int i=0; i<nAlgs; i++){
			meanHV=meanS=stdHV=stdS=0;
			
			for(int j=0; j<size; j++){
				meanHV += rankingsHV.get(i).get(j);
				stdHV += rankingsHV.get(i).get(j)*rankingsHV.get(i).get(j);
			}
			
			for(int j=0; j<size; j++){
				meanS += rankingsS.get(i).get(j);
				stdS += rankingsS.get(i).get(j)*rankingsS.get(i).get(j);
			}
			
			meanHV /= size;
			meanS /= size;
			stdHV = Math.sqrt((stdHV / size) - (meanHV*meanHV));
			stdS = Math.sqrt((stdS / size) - (meanS*meanS));
			
			sb.append(algName[i] + ": HV => mean=" + meanHV + " std=" + stdHV + " S => mean="+meanS+" std="+stdS+"\n");
		}
		
		FileWriter writer;
		try {
			writer = new FileWriter(dirname + "\\" + dir.getName() + ".txt");
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
