package net.sf.jclec.sbse.cl2cmp.mo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.InstanceIterator;
//import es.uco.kdis.datapro.dataset.Column.IntegerColumn;
import es.uco.kdis.datapro.dataset.Column.MultivalueColumn;
import es.uco.kdis.datapro.dataset.Source.CsvDataset;
//import es.uco.kdis.datapro.datatypes.InvalidValue;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.MOEvaluator;
import net.sf.jclec.mo.fitness.Objective;
import net.sf.jclec.syntaxtree.SyntaxTree;
import net.sf.jclec.sbse.cl2cmp.mo.objectives.Cl2CmpObjective;

/**
 * Evaluator for multi/many-objective algorithms
 * in the Cl2Cmp ('Classes to Components') problem.
 * 
 * <p>History:
 * <ul>
 *  <li>2.1: New dataset with abstract classes and new method (setAbstractClasses) (June 2014)
 * 	<li>2.0: Now extending from MOEvaluator (May 2014)
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 2.0
 * @see MOEvaluator
 * */
public class Cl2CmpMoEvaluator extends MOEvaluator {

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------------- Properties
	//////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -4279911861830083711L;

	/** Dataset that stores the information about relations */
	private Dataset relDataset;

	/** Dataset file name (relations) */
	private String instanceName;

	/** Dataset that stores the information about classes */
	private Dataset classDataset;

	/** Number of classes */
	private int numberOfClasses;

	/** Array with the indexes of classes in a component  */
	private transient ArrayList<Integer> indexes = new ArrayList<Integer>();

	/** Array of nodes visited used the graph depth function  */
	private transient boolean visited [];

	/** Classes and its correspondent group inside the component */
	private transient int classGroup [];

	/** Maximum number of components */
	private int maxComponents;

	/** Minimum number of components */
	private int minComponents;

	/** Maximum number of interfaces */
	private int maxInterfaces;

	//////////////////////////////////////////////////////////////////
	//--------------------------------------------------- Constructors
	//////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public Cl2CmpMoEvaluator(){
		super();
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Get/Set methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Get dataset file name
	 * @return dataset file name
	 * */
	public String getDatasetFileName(){
		return this.instanceName;
	}

	/**
	 * Set dataset file name
	 * @param filename Dataset file name
	 * */
	protected void setDatasetFileName(String filename){
		this.instanceName = filename;
	}

	/**
	 * Get the maximum number
	 * of components
	 * @return maximum number
	 * of components configured
	 * */
	public int getMaxComponents(){
		return this.maxComponents;
	}

	/**
	 * Set the minimum number
	 * of components
	 * @param minComponents The
	 * maximum number of components
	 * */
	public void setMaxComponents(int minComponents){
		this.minComponents = minComponents;
	}

	/**
	 * Get the minimum number
	 * of components
	 * @return minimum number
	 * of components configured
	 * */
	public int getMinComponents(){
		return this.minComponents;
	}

	/**
	 * Set the minimum number
	 * of components
	 * @param minComponents The
	 * minimum number of components
	 * */
	public void setMinComponents(int minComponents){
		this.minComponents = minComponents;
	}

	/**
	 * Get the maximum number
	 * of candidate interfaces
	 * @return maximum number
	 * of interfaces extracted
	 * from the model
	 * */
	public int getMaxInterfaces(){
		return this.maxInterfaces;
	}

	/**
	 * Set the maximum number
	 * of candidate interfaces
	 * @param maxInterfaces The
	 * maximum number of components
	 * */
	public void setMaxInterfaces(int maxInterfaces){
		this.maxInterfaces = maxInterfaces;
	}

	public void setProblemCharacteristics(int minComponents, int maxComponents, int maxInterfaces){
		setMinComponents(minComponents);
		setMaxComponents(maxComponents);
		setMaxInterfaces(maxInterfaces);

		// Configure in the objectives
		for(Objective obj: this.objectives){
			((Cl2CmpObjective)obj).setDataset(relDataset);
			((Cl2CmpObjective)obj).setMaxComponents(maxComponents);
			((Cl2CmpObjective)obj).setMinComponents(minComponents);
			((Cl2CmpObjective)obj).setMaxInterfaces(maxInterfaces);
			((Cl2CmpObjective)obj).computeBounds();
		}
		
		/*for(Objective obj: this.objectives){
			System.out.println(((Cl2CmpObjective)obj).getName() + " "
					+ ((Cl2CmpObjective)obj).isMaximized() + " "
					+ ((Cl2CmpObjective)obj).getInterval().getLeft()+ " "
					+ ((Cl2CmpObjective)obj).getInterval().getRight());
		}*/
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	@Override
	public void configure(Configuration settings) {

		// Call super class configuration method
		super.configure(settings);
	}

	@Override
	protected void evaluate(IIndividual ind) {
		Cl2CmpMoIndividual emoInd = (Cl2CmpMoIndividual)ind;

		// The individual has not been evaluated yet
		if (emoInd.getFitness() == null){
			computeMeasures(emoInd);	// Measures needed for objectives evaluation
			super.evaluate(emoInd);		// Evaluate the individual
		}
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Private methods
	//////////////////////////////////////////////////////////////////

	// --------------------------- Private configuration methods
	/**
	 * Configure settings related to the
	 * problem instance (dataset)
	 * @param settings: Configuration object
	 * @throws IllegalFormatSpecificationException 
	 * @throws NotAddedValueException 
	 * @throws IOException 
	 * @throws IndexOutOfBoundsException 
	 * */
	public void configureDatasets(String datasetRel, String datasetCls, int numOfClasses) 
			throws IndexOutOfBoundsException, IOException, NotAddedValueException, IllegalFormatSpecificationException {
		this.numberOfClasses = numOfClasses;
		if(datasetRel!=null){
			readRelationsDataset(datasetRel, numOfClasses);
			setDatasetFileName(datasetRel);
		}
		if(datasetCls!=null){
			readClassesDataset(datasetCls);
		}
	}

	/**
	 * Open and read the dataset
	 * @param path: Path to dataset file
	 * @param classes: Number of classes
	 * @throws IllegalFormatSpecificationException 
	 * */
	private void readRelationsDataset(String path, int classes) 
			throws IndexOutOfBoundsException, IOException, NotAddedValueException, IllegalFormatSpecificationException {
		this.numberOfClasses = classes;
		this.relDataset = new CsvDataset(path);
		this.relDataset.setMissingValue("?");
		String format = "";
		for(int i=0; i<classes; i++){
			format += "m";
		}
		((CsvDataset)this.relDataset).readDataset("nv", format);
	}

	/**
	 * Open and read the dataset
	 * @param path: Path to dataset file
	 * @param classes: Number of classes
	 * @throws IllegalFormatSpecificationException 
	 * */
	private void readClassesDataset(String path) 
			throws IndexOutOfBoundsException, IOException, NotAddedValueException, IllegalFormatSpecificationException {
		this.classDataset = new CsvDataset(path);
		this.classDataset.setMissingValue("?");
		String format = "sim";	// Nominal (class name) + Integer (abstract) + Multicolumn (data types)
		((CsvDataset)this.classDataset).readDataset("nv", format);
	}

	// --------------------------- Private evaluation methods

	/**
	 * Compute general measures over an individual
	 * @param ind: The individual
	 * */
	private void computeMeasures(Cl2CmpMoIndividual ind){
		// Set required information for evaluation objectives
		this.setClassesDistribution(ind);
		this.setClassesInGroupsDistribution(ind);
		this.setNumberExternalRelations(ind);
		//this.setNumberOfDataTypes(ind);
		this.setAbstractClasses(ind);
	}

	/** 
	 * Set the correspondence between each class and its component 
	 * @param ind: The individual
	 * */
	private void setClassesDistribution(Cl2CmpMoIndividual ind){

		int actualCmp=-1, index;
		boolean isClass = false, isConnector = false;
		String symbol;
		// Number of classes in each component
		int componentNumClasses [] = new int[ind.getNumberOfComponents()];

		// Component at each class belong
		int distribution [] = new int[this.numberOfClasses];
		SyntaxTree genotype = ind.getGenotype();

		for(int i=1; !isConnector; i++){

			symbol = genotype.getNode(i).getSymbol();

			// Non terminal node
			if(genotype.getNode(i).arity()!=0){
				// New set of classes
				if(symbol.equalsIgnoreCase("classes")){
					isClass=true;
					actualCmp++;
				}
				else if(symbol.equalsIgnoreCase("required-interfaces")){
					isClass=false;
				}
				else if(symbol.equalsIgnoreCase("connectors")){
					isConnector=true;
				}
			}
			// Terminal node
			else if(isClass){

				// Increment the component number of classes
				componentNumClasses[actualCmp]++;

				// Set the component at which the class belongs
				index = this.relDataset.getIndexOfColumn(this.relDataset.getColumnByName(symbol));
				distribution[index] = actualCmp;
			}
		}// end of tree route

		// Set distribution in the individual
		ind.setClassesDistribution(distribution);
		ind.setNumberOfClasses(componentNumClasses);
	}

	/**
	 * Set the classes/group correspondence in each component
	 * @param ind: The individual 
	 * */
	private void setClassesInGroupsDistribution(Cl2CmpMoIndividual ind){
		// Get genotype
		SyntaxTree genotype = ind.getGenotype();

		int numberOfComponents = ind.getNumberOfComponents();
		int numberOfClasses = this.relDataset.getColumns().size();

		int classIndex, actualCmp=-1;
		boolean isClass = false, isConnector = false;
		MultivalueColumn column;
		String symbol;

		// Initialize
		this.classGroup = new int [numberOfClasses];
		int componentNumGroups[] = new int[numberOfComponents];

		// Compute metrics for each component in the individual
		for(int i=1; !isConnector; i++){

			symbol = genotype.getNode(i).getSymbol();

			// Non terminal node
			if(genotype.getNode(i).arity()!=0){
				// The symbol classes indicates the beginning of a 
				// group of classes in a component
				if(symbol.equalsIgnoreCase("classes")){
					isClass=true;
					actualCmp++;
					// New set of classes
					this.indexes.clear();
				}
				else if(symbol.equalsIgnoreCase("required-interfaces")){
					isClass=false;
					// End of component classes, compute the number of groups
					componentNumGroups[actualCmp] = this.numberOfGroups();
				}
				else if(symbol.equalsIgnoreCase("connectors")){
					isConnector=true;
				}
			}
			// Terminal node
			else if(isClass){

				// Get the dataset information about the class
				column = (MultivalueColumn) this.relDataset.getColumnByName(symbol);
				classIndex = this.relDataset.getIndexOfColumn(column);

				// Add the class index in the array of indexes
				this.indexes.add(classIndex);
			}
		}// end of tree route

		// Set on individual
		ind.setClassesToGroups(this.classGroup);
		ind.setNumberOfGroups(componentNumGroups);
	}

	/**
	 * Counts the number of external relations on each component
	 * @param ind: The individual
	 * */ 
	private void setNumberExternalRelations(Cl2CmpMoIndividual ind) {
		// Get genotype
		SyntaxTree genotype = ind.getGenotype();
		int numberOfComponents = ind.getNumberOfComponents();

		int j, actualIndex, classIndex, actualCmp=-1, otherCmp=-1;
		boolean isClass = false, isOtherClass = false, isConnector = false;
		MultivalueColumn column;
		int nav_ij, nav_ji;
		String symbol;

		// Initialize
		int componentNumberExternalConnections [] = new int[numberOfComponents];

		for(int i=0; i<numberOfComponents; i++){
			componentNumberExternalConnections[i] = 0;
		}

		// Compute needed metrics for each component
		for(int i=1; !isConnector; i++){

			symbol = genotype.getNode(i).getSymbol();

			// Non terminal node
			if(genotype.getNode(i).arity()!=0){
				// The symbol classes indicates the beginning of a new component
				if(symbol.equalsIgnoreCase("classes")){
					isClass=true;
					actualCmp++;
				}
				else if(symbol.equalsIgnoreCase("required-interfaces")){
					isClass=false;
				}
				else if(symbol.equalsIgnoreCase("connectors")){
					isConnector=true;
				}
			}

			// Terminal node
			else{
				// If the terminal is a class
				if(isClass){

					// Get the dataset information about the class
					column = (MultivalueColumn) this.relDataset.getColumnByName(symbol);
					actualIndex = this.relDataset.getIndexOfColumn(column);

					// Check the relations with classes belonging to other components
					otherCmp=actualCmp;			// Start in the actual component
					j=i+1;

					while(!(genotype.getNode(j).getSymbol().equalsIgnoreCase("connectors"))){

						// Search a class in the other component
						if((genotype.getNode(j-1).getSymbol().equalsIgnoreCase("classes"))){
							isOtherClass=true;
							otherCmp++;
						}

						if(isOtherClass){
							// Get relations between classes
							classIndex = this.relDataset.getIndexOfColumn(this.relDataset.getColumnByName(genotype.getNode(j).getSymbol()));
							ArrayList<Object> relations1 = (ArrayList<Object>) column.getMultiElement(classIndex);

							// Not an invalid value
							if(relations1.size()>1){

								ArrayList<Object> relations2 = (ArrayList<Object>)((MultivalueColumn)this.relDataset.getColumn(classIndex)).getMultiElement(actualIndex);

								// Check type and navigation of each relation
								for(int k=1; k<relations1.size(); k+=2){

									nav_ij = Integer.parseInt((String)relations1.get(k));
									nav_ji = Integer.parseInt((String)relations2.get(k));

									// Not a candidate interface, because its a bidirectional relation
									if(nav_ij==nav_ji){
										componentNumberExternalConnections[actualCmp]++;
										componentNumberExternalConnections[otherCmp]++;
									}
								}
							}
							// End of classes in the other component
							if(genotype.getNode(j+1).arity()!=0){
								isOtherClass=false;
							}
						}
						j++;
					}
				}
			}
		}// end of tree route

		ind.setExternalConnections(componentNumberExternalConnections);
	}

	//@SuppressWarnings("unchecked")
	/*private void setNumberOfDataTypes(Cl2CmpMoIndividual ind){
		int numOfComponents = ind.getNumberOfComponents();
		int [] numOfDistinctTypes = new int [numOfComponents];

		ArrayList<ArrayList<String>> distinctTypes = new ArrayList<ArrayList<String>>(numOfComponents);
		int classDistribution [] = ind.getClassesDistribution();
		int numOfClasses = classDistribution.length;
		int componentIndex;
		List<String> datatypes;
		MultivalueColumn col = (MultivalueColumn) classDataset.getColumn(2);
		int size;
		Object value;
		String type;

		for(int i=0; i<numOfComponents; i++){
			distinctTypes.add(new ArrayList<String>());
		}

		for(int i=0; i<numOfClasses; i++){
			datatypes = (ArrayList<String>)col.getElement(i);
			componentIndex = classDistribution[i];
			size = datatypes.size();
			for(int j=0; j<size; j++){
				value = datatypes.get(j);
				if(!(value instanceof InvalidValue)){
					type = value.toString();
					if(!distinctTypes.get(componentIndex).contains(type)){
						distinctTypes.get(componentIndex).add(type);
					}
				}
			}
		}

		for(int i=0; i<numOfComponents; i++){
			// Save number of distinct data type references
			numOfDistinctTypes[i] = distinctTypes.get(i).size();
		}

		ind.setNumberOfDataTypes(numOfDistinctTypes);
	}*/

	private void setAbstractClasses(Cl2CmpMoIndividual ind){
		String className;
		int index;
		int component;
		int numOfComponents = ind.getNumberOfComponents();
		int numOfAbstractClasses [] = new int [numOfComponents];
		int classDistribution [] = ind.getClassesDistribution();
		for(int i=0; i<numOfComponents; i++){
			numOfAbstractClasses[i] = 0;
		}

		InstanceIterator it = new InstanceIterator(classDataset);
		List<Object> instance;
		int isAbstract;

		// Clases no ordenadas
		while(!it.isDone()){
			instance = it.currentInstance();

			className = instance.get(0).toString();
			isAbstract = Integer.parseInt((instance.get(1).toString()));

			// The class is abstract
			if(isAbstract == 1){

				// Get the index of the class
				index = relDataset.getIndexOfColumn(relDataset.getColumnByName(className));

				// Get the corresponding component
				component = classDistribution[index];
				numOfAbstractClasses[component]++;
			}
			it.next();
		}

		// Clases ordenadas
		/*IntegerColumn col = (IntegerColumn)classDataset.getColumn(1);
		for(int i=0; i<numberOfClasses; i++){
			//System.out.println("Clase: " + classDataset.getColumn(0).getElement(i) + " isAbstract: " + col.getElement(i));
			isAbstract = ((Integer)col.getElement(i)).intValue();
			if(isAbstract == 1){
				component = classDistribution[i];
				numOfAbstractClasses[component]++;
			}
		}*/

		/*for(int i=0; i<numOfComponents; i++){
			System.out.println("Componente " + i + " -> #abstractas: " + numOfAbstractClasses[i]);
		}*/

		ind.setNumberOfAbstractClasses(numOfAbstractClasses);
	}

	/**
	 * Compute the number of connected component
	 * in the graph formed with the actual 
	 * <code>indexes</code>.
	 * @return Number of connected component
	 * */
	private int numberOfGroups() {
		int i, numOfGroups = 0;
		int size = this.indexes.size();
		this.visited = new boolean[size];

		for(i=0; i<size; i++)
			this.visited[i]=false;

		for(i=0; i<size; i++){
			if(!this.visited[i]){
				this.classGroup[this.indexes.get(i)]=numOfGroups; // Set group number
				numOfGroups++;
				graphDepthPath(i, this.indexes.get(i));
			}
		}
		return numOfGroups;
	}

	/**
	 * Realize a graph depth search marking 
	 * visited nodes.
	 * @param actualNode The node origin.
	 * @param classIndex The column index in the dataset for the origin node.
	 * */
	private void graphDepthPath(int actualNode, int classIndex){
		int j, size = this.visited.length;
		// Now, the node is visited
		this.visited[actualNode]=true;
		for(j=0; j<size; j++){
			// The node origin is connected with the node j, not visited yet, continue recursive depth search with j
			if( ((MultivalueColumn)this.relDataset.getColumn(classIndex)).getMultiElement(this.indexes.get(j)).size() > 1
					&& !this.visited[j]){

				this.classGroup[this.indexes.get(j)]=this.classGroup[classIndex];	// Set its group number
				graphDepthPath(j, this.indexes.get(j));
			}
		}
	}
}