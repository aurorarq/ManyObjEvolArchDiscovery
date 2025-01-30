package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;

public class DeleteFiles {

	public static void main(String[] args) {
		String dirname = "G:\\Aurora\\Documentos\\Universidad\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\rawdata\\6obj-4";
		String dirname2;
		File dir = new File(dirname), dir2;


		if(dir.exists()){

			File [] combi = dir.listFiles();
			for(File c: combi){
				File [] problems = c.listFiles();
				for(File p: problems){
					if(p.isDirectory()){
						dirname2 = p.getAbsolutePath() + "\\res-evol";
						dir2 = new File(dirname2);
						if(dir2.exists()){
							File [] algs = dir2.listFiles();
							for(File a: algs){
								if(a.isDirectory()){
									File [] files = a.listFiles();

									for(File f: files){
										f.delete();
									}
								}
								a.delete();
							}
						}
						dir2.delete();
					}
				}
			}
		}

	}

}
