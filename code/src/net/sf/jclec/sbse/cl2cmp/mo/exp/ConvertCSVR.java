package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.util.List;

import es.uco.kdis.datapro.dataset.Dataset;
//import es.uco.kdis.datapro.dataset.InstanceIterator;
import es.uco.kdis.datapro.dataset.Column.ColumnAbstraction;
import es.uco.kdis.datapro.dataset.Column.NominalColumn;
import es.uco.kdis.datapro.dataset.Column.NumericalColumn;
import es.uco.kdis.datapro.dataset.Source.CsvDataset;
import es.uco.kdis.datapro.dataset.Source.ExcelDataset;
import es.uco.kdis.datapro.datatypes.InvalidValue;

public class ConvertCSVR {

	public static void main(String[] args) {

		String filename; //"cb-abs-emo-indicators2.xlsx";//"icd-erp-gcr-ins-cs-en-cl-cb-abs-emo-indicators2.xlsx";
		String dirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\res-indicadores\\9obj";
		String resdirname = "C:\\Users\\Aurora\\Documents\\KDIS\\revistas\\2014 Comparativa many\\experimentación\\effect-size\\datos";
		int nObjs = 9;
		String combi = "", algorithm, instance;
		List<ColumnAbstraction> columns;
		int size;
		double valueHV, valueS;

		File [] files = (new File(dirname)).listFiles();

		for(File f: files){

			filename = f.getName();

			// OPEN SOURCE
			String [] aux = filename.split("-");
			combi = "";
			for(int i=0; i<aux.length-3; i++)
				combi += aux[i] + "-";
			combi += aux[aux.length-3];
			System.out.println(combi);
			
			//if(combi.equalsIgnoreCase("ins-cs")){
				
				Dataset datasetIn = new ExcelDataset(f.getAbsolutePath());
				((ExcelDataset)datasetIn).setNullValue("#NUM!");
				//((ExcelDataset)datasetIn).setNullValue("?");
				try{
					((ExcelDataset)datasetIn).readDataset("nv", "sf%f%");
				}catch(Exception e){
					e.printStackTrace();
					//System.exit(-1);
				}

				// RESULTS HV
				Dataset datasetOutHV = new CsvDataset();
				datasetOutHV.setNumberOfDecimals(6);
				datasetOutHV.addColumn(new NominalColumn("Problem"));
				datasetOutHV.addColumn(new NumericalColumn("SPEA2"));
				datasetOutHV.addColumn(new NumericalColumn("NSGA2"));
				datasetOutHV.addColumn(new NumericalColumn("Cl2CmpMOEAD"));
				datasetOutHV.addColumn(new NumericalColumn("SSeMOEA"));
				datasetOutHV.addColumn(new NumericalColumn("GrEA"));
				datasetOutHV.addColumn(new NumericalColumn("IBEAe"));
				datasetOutHV.addColumn(new NumericalColumn("HypE"));
				datasetOutHV.addColumn(new NumericalColumn("NSGA3"));

				// RESULTS HV
				Dataset datasetOutS = new CsvDataset();
				datasetOutS.setNumberOfDecimals(6);
				datasetOutS.addColumn(new NominalColumn("Problem"));
				datasetOutS.addColumn(new NumericalColumn("SPEA2"));
				datasetOutS.addColumn(new NumericalColumn("NSGA2"));
				datasetOutS.addColumn(new NumericalColumn("Cl2CmpMOEAD"));
				datasetOutS.addColumn(new NumericalColumn("SSeMOEA"));
				datasetOutS.addColumn(new NumericalColumn("GrEA"));
				datasetOutS.addColumn(new NumericalColumn("IBEAe"));
				datasetOutS.addColumn(new NumericalColumn("HypE"));
				datasetOutS.addColumn(new NumericalColumn("NSGA3"));

				// GET DATA
				columns = datasetIn.getColumns();
				size = columns.get(0).getSize();

				for(int i=0; i<size; i++){
					aux = ((String)columns.get(0).getElement(i)).split("-");
					instance = aux[0];
					algorithm = aux[1];

					if(columns.get(1).getElement(i) instanceof InvalidValue)
						valueHV = -1.0;
					else
						valueHV = ((Double)columns.get(1).getElement(i)).doubleValue();

					if(columns.get(2).getElement(i) instanceof InvalidValue)
						valueS = -1.0;
					else
						valueS = ((Double)columns.get(2).getElement(i)).doubleValue();

					//if(!instance.equalsIgnoreCase("nekohtml")){
						if(i%8==0){
							datasetOutHV.getColumn(0).addValue(instance);
							datasetOutS.getColumn(0).addValue(instance);
						}
						datasetOutHV.getColumnByName(algorithm).addValue(valueHV);
						datasetOutS.getColumnByName(algorithm).addValue(valueS);	
					//}
				}

				// FIX COLUMN NAMES
				datasetOutHV.getColumn(2).setName("NSGA-II");
				datasetOutHV.getColumn(3).setName("MOEA/D");
				datasetOutHV.getColumn(4).setName("eMOEA");
				datasetOutHV.getColumn(6).setName("IBEA");
				datasetOutHV.getColumn(8).setName("NSGA-III");

				datasetOutS.getColumn(2).setName("NSGA-II");
				datasetOutS.getColumn(3).setName("MOEA/D");
				datasetOutS.getColumn(4).setName("eMOEA");
				datasetOutS.getColumn(6).setName("IBEA");
				datasetOutS.getColumn(8).setName("NSGA-III");

				// WRITE OUTPUT
				try{
					((CsvDataset)datasetOutHV).writeDataset(resdirname + "/" + nObjs + "objs" +"/" + combi+"-hv.csv");
					((CsvDataset)datasetOutS).writeDataset(resdirname + "/" + nObjs + "objs" + "/" + combi+"-s.csv");
				}catch(Exception e){
					e.printStackTrace();
					//System.exit(-1);
				}
			//}
		}
	}
}