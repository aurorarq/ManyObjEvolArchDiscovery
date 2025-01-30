package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TexTables {

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

		String dirname = "C:\\Users\\Aurora\\Desktop\\tests";
		//String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-tests\\8obj+nsga3";
		File dir = new File(dirname);
		File [] files;
		TexTables parser = new TexTables();
		StringBuffer sb = new StringBuffer();
		
		if(dir.isDirectory()){
			files = dir.listFiles();

			for(File f: files){
				
				if(f.getName().contains("-hv")){
					System.out.println(f.getName());
					ArrayList<AlgorithmTestResult> results = parser.readValues(f.getAbsolutePath(), f.getAbsolutePath().replace("-hv", "-s"));
					String line = parser.writeResult(f.getName().substring(0, f.getName().lastIndexOf("-")), results);
					sb.append(line + "\n\n");
				}
			}
			
			FileWriter writer;
			try {
				writer = new FileWriter(dirname + "\\" + dir.getName() + ".tex");
				writer.write(sb.toString());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public ArrayList<AlgorithmTestResult> readValues(String filenameHv, String filenameS){
		ArrayList<AlgorithmTestResult> results = new ArrayList<AlgorithmTestResult>();
		AlgorithmTestResult result = null;
		String line;
		String [] aux;
		double threshold;

		try {
			///////////// HV 
			BufferedReader reader = new BufferedReader(new FileReader(filenameHv));
			line = reader.readLine();

			while(!line.contains("<tr><td>i</td><td>")){
				line = reader.readLine();
			}
			line = reader.readLine();	// skip "</tr>"
			line = reader.readLine(); 	// skip "<tr>"

			while(!line.equalsIgnoreCase("</tr></table>")){

				if(line.contains("<td>")){
					aux = line.split("</td><td>");

					result = new AlgorithmTestResult();

					result.setName(aux[1]); // name
					result.setHv(Double.parseDouble(aux[2])); // hv value

					if(aux[5].contains("&nbsp;")){
						result.setHvAlpha(-1);
						result.setHvType(1);
					}

					else{
						result.setHvAlpha(Double.parseDouble(aux[5].substring(0, aux[5].indexOf("<")))); // hv alpha
						result.setHvType(0);
					}
					results.add(result);
				}
				line = reader.readLine();
			}

			// HV Threshold
			while(!line.contains("test rejects")){
				line = reader.readLine();
			}
			aux = line.split("<");
			try{
				threshold = Double.parseDouble(aux[2].substring(0, aux[2].length()-1));
			}catch(NumberFormatException e){
				threshold = Double.MAX_VALUE;
			}
			reader.close();

			// SET TYPES
			boolean signWorse = false;
			for(AlgorithmTestResult atr: results){
				if(atr.getHvType()==0){
					if(atr.getHvAlpha() < threshold){
						atr.setHvType(-1);
						signWorse = true;
					}
				}
			}
			if(signWorse){
				for(AlgorithmTestResult atr: results){
					if(atr.getHvType()==1){
						atr.setHvType(2);
					}
				}
			}

			///////////// SPACING
			reader = new BufferedReader(new FileReader(filenameS));
			line = reader.readLine();

			while(!line.contains("<tr><td>i</td><td>")){
				line = reader.readLine();
			}
			line = reader.readLine();	// skip "</tr>"
			line = reader.readLine(); 	// skip "<tr>"
			
			int index = -1;
			while(!line.equalsIgnoreCase("</tr></table>")){

				if(line.contains("<td>")){
					aux = line.split("</td><td>");

					for(int i=0; i<results.size(); i++){
						if (results.get(i).getName().equalsIgnoreCase(aux[1])){
							index = i;
							break;
						}
					}
					
					results.get(index).setSpacing(Double.parseDouble(aux[2])); // hv value

					if(aux[5].contains("&nbsp;")){
						results.get(index).setSpacingAlpha(-1);
						results.get(index).setsType(1);
					}

					else{
						results.get(index).setSpacingAlpha(Double.parseDouble(aux[5].substring(0, aux[5].indexOf("<")))); // hv alpha
						results.get(index).setsType(0);
					}
				}
				line = reader.readLine();
			}

			// S Threshold
			while(!line.contains("test rejects")){
				line = reader.readLine();
			}
			aux = line.split("<");
			try{
				threshold = Double.parseDouble(aux[2].substring(0, aux[2].length()-1));
			}catch(NumberFormatException e){
				threshold = Double.MAX_VALUE;
			}
			reader.close();

			// SET TYPES
			signWorse = false;
			for(AlgorithmTestResult atr: results){
				if(atr.getsType()==0){
					if(atr.getSpacingAlpha() < threshold){
						atr.setsType(-1);
						signWorse = true;
					}
				}
			}
			if(signWorse){
				for(AlgorithmTestResult atr: results){
					if(atr.getsType()==1){
						atr.setsType(2);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public String writeResult(String combi, ArrayList<AlgorithmTestResult> results){
		String texLine = "";
		
		texLine += combi + "\t&";
		double value;
		double dExp = Math.pow(10, 2);
		String valueString;
		String extra = "";
		
		// SPEA2
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("SPEA2")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} &"; break;
				case 0: texLine += " " + value+extra + " &"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				break;
			}
		}
		
		// NSGA-II
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("NSGA2")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} &"; break;
				case 0: texLine += " " + value+extra + " &"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				break;
			}
		}
		
		// MOEAD
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("Cl2CmpMOEAD")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} &"; break;
				case 0: texLine += " " + value+extra + " &"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				break;
			}
		}
		
		// eMOEA
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("SSeMOEA")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} &"; break;
				case 0: texLine += " " + value+extra + " &"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				break;
			}
		}
		
		// GrEA
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("GrEA")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} &"; break;
				case 0: texLine += " " + value+extra + " &"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				break;
			}
		}
		
		// IBEA
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("IBEAe")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} &"; break;
				case 0: texLine += " " + value+extra + " &"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				break;
			}
		}
		
		// HypE
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("HypE")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} &"; break;
				case 0: texLine += " " + value+extra + " &"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				break;
			}
		}
		
		// NSGA-III
		for(AlgorithmTestResult atr: results){
			if(atr.getName().equalsIgnoreCase("NSGA3")){
				// HV
				value = (Math.round(atr.getHv()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getHvType()){
				case -1: texLine += " \\textit{"+value + extra +"} &"; break;
				case 0: texLine += " " + value + extra + " &"; break;
				case 1: texLine += " \\textbf{"+value + extra+"} &"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} &"; break;
				}
				
				// S
				value = (Math.round(atr.getSpacing()*dExp)/dExp);
				valueString = ""+value+"";
				if(valueString.length()==3){
					extra = "0";
				}
				else
					extra = "";
				switch(atr.getsType()){
				case -1: texLine += " \\textit{"+value+extra+"} \\\\"; break;
				case 0: texLine += " " + value+extra + " \\\\"; break;
				case 1: texLine += " \\textbf{"+value+extra+"} \\\\"; break;
				case 2: texLine += " \\cellcolor[gray]{0.8}\\textbf{"+value+extra+"} \\\\"; break;
				}
				break;
			}
		}
		
		return texLine;
	}
}
