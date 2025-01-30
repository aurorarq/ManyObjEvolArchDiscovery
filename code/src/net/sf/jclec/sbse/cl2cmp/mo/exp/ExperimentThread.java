package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.util.List;

import net.sf.jclec.IAlgorithm;
import net.sf.jclec.IConfigure;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class ExperimentThread extends Thread {
	private List<String> filenames;
	public ExperimentThread(List<String> filenames) {
		this.filenames = filenames;
	}
			
	@SuppressWarnings("unchecked")
	public void run()
	{
		int size = filenames.size();
		for(int i=0; i<size; i++){
			// Try open job file
			File jobFile = new File(filenames.get(i));
			
			if (jobFile.exists()) {
				try {
					//System.out.println("Algorithm started");
					// Job configuration
					XMLConfiguration jobConf = new XMLConfiguration(jobFile);
					// Process header
					String header = "process";
					// Create and configure algorithms
					String aname = jobConf.getString(header+"[@algorithm-type]");
					Class<IAlgorithm> aclass = (Class<IAlgorithm>) Class.forName(aname);
					IAlgorithm algorithm = aclass.newInstance();
					// Configure runner
					if (algorithm instanceof IConfigure) {
						((IConfigure) algorithm).configure(jobConf.subset(header));
					}
					// Execute algorithm runner
					algorithm.execute();
					//System.out.println("Algorithm finished");
				}
				catch (ConfigurationException e) {
					System.out.println("Configuration exception ");
				}			
				catch (Exception e) {
					e.printStackTrace();
				}			
			}
			else {
				System.out.println("Job file not found");
				System.exit(1);			
			}
		}
	}
}
