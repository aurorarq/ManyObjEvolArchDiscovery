package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.io.IOException;

import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class SwapColumns4objs {

	public static void main(String[] args) {
		String dirname1 = "C:\\Users\\Aurora\\Documents\\KDIS\\congresos\\2014_GECCO\\experimentación\\res-fronts";
		String dirObj = "4obj-11-15/g-s-i-e";
		String dirRes = "4obj-11-15/g-i-s-e";



		// G-S-I-E => G-I-S-E
		File dir = new File(dirname1);
		File [] problems = dir.listFiles();
		File dir2, dir3;
		File [] fronts;
		ExcelDataset dataset, dataset2;

		for(File p: problems){
			dir2 = new File(p.getAbsolutePath()+"/"+dirObj);
			dir3 = new File(p.getAbsoluteFile()+"/"+dirRes);
			if(!dir3.exists())
				dir3.mkdirs();

			fronts = dir2.listFiles();

			
			for(File f: fronts){

				//if(f.getName().contains("moead")||f.getName().contains("emoea")){
				if(!f.getName().contains("nsga2") && !f.getName().contains("grea-java2html")){
					System.out.println(f.getName());
					dataset = new ExcelDataset(f.getAbsolutePath());
					
					try{	
						dataset.readDataset("nv","ffff");
					
					String [] aux = f.getName().split("-");
					String resname = aux[0]+"-"+aux[1]+"-g-i-s-e-"+aux[aux.length-1]+".xlsx";
					dataset2 = new ExcelDataset();
					dataset2.addColumn(dataset.getColumn(0));
					dataset2.addColumn(dataset.getColumn(2));
					dataset2.addColumn(dataset.getColumn(1));
					dataset2.addColumn(dataset.getColumn(3));

					dataset2.writeDataset(dir3.getAbsolutePath()+"/"+resname);
									
					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException
							| IllegalFormatSpecificationException e) {
						// TODO Auto-generated catch block
						System.out.println(f.getName());
						e.printStackTrace();
					}

				}
			}

		}

		// P-G-S-I => P-G-I-S		 
		dirObj = "4obj-11-15/p-g-i-s";
		dirRes = "4obj-11-15/p-g-i-s2";
		problems = dir.listFiles();

		for(File p: problems){
			dir2 = new File(p.getAbsolutePath()+"/"+dirObj);
			dir3 = new File(p.getAbsoluteFile()+"/"+dirRes);
			if(!dir3.exists())
				dir3.mkdirs();

			fronts = dir2.listFiles();

			for(File f: fronts){
				if(!f.getName().contains("nsga2")&& !f.getName().contains("grea-java2html")){
				//if(f.getName().contains("moead")||f.getName().contains("emoea")){
					System.out.println(f.getName());
					dataset = new ExcelDataset(f.getAbsolutePath());
					try {
						dataset.readDataset("nv","ffff");
					

					String [] aux = f.getName().split("-");
					String resname = aux[0]+"-"+aux[1]+"-p-g-i-s-"+aux[aux.length-1]+".xlsx";
					dataset2 = new ExcelDataset();
					dataset2.addColumn(dataset.getColumn(0));
					dataset2.addColumn(dataset.getColumn(1));
					dataset2.addColumn(dataset.getColumn(3));
					dataset2.addColumn(dataset.getColumn(2));

					dataset2.writeDataset(dir3.getAbsolutePath()+"/"+resname);

					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException
							| IllegalFormatSpecificationException e) {
						// TODO Auto-generated catch block
						System.out.println(f.getName());
						e.printStackTrace();
					}

				}
			}

		}

		// P-G-S-I => P-G-I-S		 
		dirObj = "4obj-11-15/p-i-s-e";
		dirRes = "4obj-11-15/p-i-s-e3";
		problems = dir.listFiles();

		for(File p: problems){
			dir2 = new File(p.getAbsolutePath()+"/"+dirObj);
			dir3 = new File(p.getAbsoluteFile()+"/"+dirRes);
			if(!dir3.exists())
				dir3.mkdirs();

			fronts = dir2.listFiles();

			for(File f: fronts){
				//if(f.getName().contains("moead")||f.getName().contains("emoea")){
				if(!f.getName().contains("nsga2") && !f.getName().contains("grea-java2html")){
					System.out.println(f.getName());
					dataset = new ExcelDataset(f.getAbsolutePath());
					try {
						dataset.readDataset("nv","ffff");
					

					String [] aux = f.getName().split("-");
					String resname = aux[0]+"-"+aux[1]+"-p-i-s-e-"+aux[aux.length-1]+".xlsx";
					dataset2 = new ExcelDataset();
					dataset2.addColumn(dataset.getColumn(0));
					dataset2.addColumn(dataset.getColumn(2));
					dataset2.addColumn(dataset.getColumn(1));
					dataset2.addColumn(dataset.getColumn(3));

					dataset2.writeDataset(dir3.getAbsolutePath()+"/"+resname);
					
					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException
							| IllegalFormatSpecificationException e) {
						// TODO Auto-generated catch block
						System.out.println(f.getName());
						e.printStackTrace();
					}

				}
			}

		}
	}

}
