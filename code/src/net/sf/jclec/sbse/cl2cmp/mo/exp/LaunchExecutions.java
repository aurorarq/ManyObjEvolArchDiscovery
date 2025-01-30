package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
/*import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
*/
import net.sf.jclec.ExperimentBuilder;
import net.sf.jclec.IAlgorithm;
import net.sf.jclec.IConfigure;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * This class overrides the launch of experiments
 * in JCLEC, allowing to initialize it with a 
 * directory of XML configurations. For experimentation
 * purposes.
 * @author Aurora Ramirez Quesada
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class LaunchExecutions {

	/**
	 * Main method
	 * 
	 * @param args Configuration File
	 */
	public static void main(String[] args) {
		// First arg must be a directory name
		ExperimentBuilder builder = new ExperimentBuilder();

		System.out.println("Initializing job");

		String files [] = new File(args[0]).list();

		// Expand the processes and execute them
		for(String f: files ){
			System.out.println("File: " + f);
			for(String experiment : builder.buildExperiment(args[0]+"/"+f))
			{
				System.out.println("Algorithm started");
				executeJob(experiment);
				System.out.println("Algorithm finished");
			}
		}
		System.out.println("Job finished");
	}

	/**
	 * Execute experiment
	 * 
	 * @param jobFilename
	 */

	@SuppressWarnings("unchecked")
	private static void executeJob(String jobFilename) 
	{	
		// Try open job file
		File jobFile = new File(jobFilename);		
		if (jobFile.exists()) {
			try {
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
