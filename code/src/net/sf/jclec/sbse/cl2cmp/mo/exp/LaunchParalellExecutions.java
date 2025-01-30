package net.sf.jclec.sbse.cl2cmp.mo.exp;

import java.io.File;
import java.util.List;

import net.sf.jclec.ExperimentBuilder;

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
public class LaunchParalellExecutions {

	/**
	 * Main method
	 * 
	 * @param args Configuration File
	 */
	public static void main(String[] args) {
		// First arg must be a directory name
		ExperimentBuilder builder = new ExperimentBuilder();
		List<String> experiments;
		System.out.println("Initializing job");

		String files [] = new File(args[0]).list();
		int nThreads = Integer.parseInt(args[1]);
		int nFilesPerThread;
		int init = 0;
		for(String f: files ){
			System.out.println("File: " + f);
			
			// Expand the processes
			experiments = builder.buildExperiment(args[0]+"/"+f);
			
			// Number of files per thread
			nFilesPerThread = (int)(experiments.size() / nThreads);
		
			// Divide the experiments
			for(int i=0; i<nThreads-1; i++){
				ExperimentThread newThread = new ExperimentThread(experiments.subList(init, init+nFilesPerThread-1));
				newThread.start();
				//System.out.println("i="+i + " init: " + init + " end: " + (init+nFilesPerThread-1));
				init += nFilesPerThread;
			}
			//System.out.println("i="+nThreads + " init: " + init + " end: " + experiments.size());
			// Last thread
			ExperimentThread newThread = new ExperimentThread(experiments.subList(init, experiments.size()));
			newThread.start();
		}
		System.out.println("Job finished");
	}
}