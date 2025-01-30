package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreateConfigurations {

	private static String metrics [] = {"icd","erp","gcr","ins","cs","enc","cl","cb","abs"};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dir = "cfg/";
		String cfgBaseName = "cfg-base-nsga3";
		String ext = ".xml";
		String dirname2;
		File dir2;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dir+cfgBaseName+ext));
			String line = reader.readLine();
			StringBuffer bufferInit = new StringBuffer();
			StringBuffer buffer;
			FileWriter writer;

			// Copy first part of configuration
			//while(!line.contains("<epsilon-values>")){
			while(!line.contains("<objectives>")){
			bufferInit.append(line + "\n");
				line=reader.readLine();
			}
			reader.close();

			String name = "";

			// 2 objectives
			dirname2 = dir+"/2obj/";
			dir2 = new File(dirname2);
			if(!dir2.exists()){
				dir2.mkdirs();
			}
			
			for(int i=0; i<metrics.length; i++){
				for(int j=i+1; j<metrics.length; j++){
					//System.out.println(metrics[i] + " - " + metrics[j] + " - " + metrics[k] + " - " + metrics[l]);

					buffer = new StringBuffer();
					buffer.append(bufferInit.toString()); // Copy the beginning of configuration

					// Epsilon values
					/*buffer.append("\t\t\t\t<epsilon-values>\n");
					buffer.append(XMLTags.epsilonTags[i]+"\n");
					buffer.append(XMLTags.epsilonTags[j]+"\n");*/
					
					// Objectives
					//buffer.append(XMLTags.evaluatorTag+"\n");
					buffer.append("\n\t\t\t<objectives>\n");
					buffer.append(XMLTags.objTags[i]+"\n");
					buffer.append(XMLTags.objTags[j]+"\n");
					
					buffer.append(XMLTags.endEvaluatorTags+"\n");
					buffer.append("\t\t<population-size>100</population-size>\n");
					buffer.append("\t\t<max-of-evaluations>10000</max-of-evaluations>\n");
					buffer.append(XMLTags.endTags); // Copy the end of configuration

					name = dirname2 + metrics[i] + "-" + metrics[j];

					// Write file
					writer = new FileWriter(name+ext);
					writer.flush();
					writer.write(buffer.toString());
					writer.close();

					// Clear buffer
					buffer = null;

				}
			}

			// 4 objectives
			dirname2 = dir+"/4obj/";
			dir2 = new File(dirname2);
			if(!dir2.exists()){
				dir2.mkdirs();
			}
			
			for(int i=0; i<metrics.length; i++){
				for(int j=i+1; j<metrics.length; j++){
					for(int k=j+1; k<metrics.length; k++){
						for(int l=k+1; l<metrics.length; l++){
							//System.out.println(metrics[i] + " - " + metrics[j] + " - " + metrics[k] + " - " + metrics[l]);

							buffer = new StringBuffer();
							buffer.append(bufferInit.toString()); // Copy the beginning of configuration

							// Epsilon values
							/*buffer.append("\t\t\t\t<epsilon-values>\n");
							buffer.append(XMLTags.epsilonTags[i]+"\n");
							buffer.append(XMLTags.epsilonTags[j]+"\n");
							buffer.append(XMLTags.epsilonTags[k]+"\n");
							buffer.append(XMLTags.epsilonTags[l]+"\n");
							buffer.append(XMLTags.evaluatorTag+"\n");*/

							// Objectives
							buffer.append("\n\t\t\t<objectives>\n");
							buffer.append(XMLTags.objTags[i]+"\n");
							buffer.append(XMLTags.objTags[j]+"\n");
							buffer.append(XMLTags.objTags[k]+"\n");
							buffer.append(XMLTags.objTags[l]+"\n");

							buffer.append(XMLTags.endEvaluatorTags+"\n");
							buffer.append("\t\t<population-size>120</population-size>\n");
							buffer.append("\t\t<max-of-evaluations>15000</max-of-evaluations>\n");
							buffer.append(XMLTags.endTags); // Copy the end of configuration

							name = dirname2 + metrics[i] + "-" + metrics[j] + "-" + metrics[k] + "-" + metrics[l];

							// Write file
							writer = new FileWriter(name+ext);
							writer.flush();
							writer.write(buffer.toString());
							writer.close();

							// Clear buffer
							buffer = null;
						}
					}
				}
			}

			// 6 objectives
			dirname2 = dir+"/6obj/";
			dir2 = new File(dirname2);
			if(!dir2.exists()){
				dir2.mkdirs();
			}
			for(int i=0; i<metrics.length; i++){
				for(int j=i+1; j<metrics.length; j++){
					for(int k=j+1; k<metrics.length; k++){
						for(int l=k+1; l<metrics.length; l++){
							for(int m=l+1; m<metrics.length; m++){
								for(int r=m+1; r<metrics.length; r++){
									//System.out.println(metrics[i] + " - " + metrics[j] + " - " + metrics[k] + " - " + metrics[l]);

									buffer = new StringBuffer();
									buffer.append(bufferInit.toString()); // Copy the beginning of configuration

									// Epsilon values
									/*buffer.append("\t\t\t\t<epsilon-values>\n");
									buffer.append(XMLTags.epsilonTags[i]+"\n");
									buffer.append(XMLTags.epsilonTags[j]+"\n");
									buffer.append(XMLTags.epsilonTags[k]+"\n");
									buffer.append(XMLTags.epsilonTags[l]+"\n");
									buffer.append(XMLTags.epsilonTags[m]+"\n");
									buffer.append(XMLTags.epsilonTags[r]+"\n");
									buffer.append(XMLTags.evaluatorTag +"\n");*/

									// Objectives
									buffer.append("\n\t\t\t<objectives>\n");
									buffer.append(XMLTags.objTags[i]+"\n");
									buffer.append(XMLTags.objTags[j]+"\n");
									buffer.append(XMLTags.objTags[k]+"\n");
									buffer.append(XMLTags.objTags[l]+"\n");
									buffer.append(XMLTags.objTags[m]+"\n");
									buffer.append(XMLTags.objTags[r]+"\n");

									buffer.append(XMLTags.endEvaluatorTags+"\n");
									buffer.append("\t\t<population-size>126</population-size>\n");
									buffer.append("\t\t<max-of-evaluations>20000</max-of-evaluations>\n");
									buffer.append(XMLTags.endTags); // Copy the end of configuration

									name = dirname2 + metrics[i] + "-" + metrics[j] + "-" + metrics[k] + "-" + metrics[l] 
											+ "-" + metrics[m] + "-" + metrics[r];

									// Write file
									writer = new FileWriter(name+ext);
									writer.flush();
									writer.write(buffer.toString());
									writer.close();

									// Clear buffer
									buffer = null;
								}
							}
						}
					}
				}
			}

			// 8 objectives
			dirname2 = dir+"/8obj/";
			dir2 = new File(dirname2);
			if(!dir2.exists()){
				dir2.mkdirs();
			}
			for(int i=0; i<metrics.length; i++){
				for(int j=i+1; j<metrics.length; j++){
					for(int k=j+1; k<metrics.length; k++){
						for(int l=k+1; l<metrics.length; l++){
							for(int m=l+1; m<metrics.length; m++){
								for(int r=m+1; r<metrics.length; r++){
									for(int s=r+1; s<metrics.length; s++){
										for(int t=s+1; t<metrics.length; t++){
											//System.out.println(metrics[i] + " - " + metrics[j] + " - " + metrics[k] + " - " + metrics[l]);

											buffer = new StringBuffer();
											buffer.append(bufferInit.toString()); // Copy the beginning of configuration

											// Epsilon values
											/*buffer.append("\t\t\t\t<epsilon-values>\n");
											buffer.append(XMLTags.epsilonTags[i]+"\n");
											buffer.append(XMLTags.epsilonTags[j]+"\n");
											buffer.append(XMLTags.epsilonTags[k]+"\n");
											buffer.append(XMLTags.epsilonTags[l]+"\n");
											buffer.append(XMLTags.epsilonTags[m]+"\n");
											buffer.append(XMLTags.epsilonTags[r]+"\n");
											buffer.append(XMLTags.epsilonTags[s]+"\n");
											buffer.append(XMLTags.epsilonTags[t]+"\n");
											buffer.append(XMLTags.evaluatorTag+"\n");*/

											// Objectives
											buffer.append("\n\t\t\t<objectives>\n");
											buffer.append(XMLTags.objTags[i]+"\n");
											buffer.append(XMLTags.objTags[j]+"\n");
											buffer.append(XMLTags.objTags[k]+"\n");
											buffer.append(XMLTags.objTags[l]+"\n");
											buffer.append(XMLTags.objTags[m]+"\n");
											buffer.append(XMLTags.objTags[r]+"\n");
											buffer.append(XMLTags.objTags[s]+"\n");
											buffer.append(XMLTags.objTags[t]+"\n");

											buffer.append(XMLTags.endEvaluatorTags+"\n");
											buffer.append("\t\t<population-size>330</population-size>\n");
											buffer.append("\t\t<max-of-evaluations>60000</max-of-evaluations>\n");
											buffer.append(XMLTags.endTags); // Copy the end of configuration

											name = dirname2 + metrics[i] + "-" + metrics[j] + "-" + metrics[k] + "-" + metrics[l] 
													+ "-" + metrics[m] + "-" + metrics[r] + "-" + metrics[s] + "-" + metrics[t];

											// Write file
											writer = new FileWriter(name+ext);
											writer.flush();
											writer.write(buffer.toString());
											writer.close();

											// Clear buffer
											buffer = null;
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
