package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.syntaxtree.SyntaxTree;
import net.sf.jclec.util.range.Closure;

/**
 * Instability Metric. Minimize the average
 * instability of the components in the
 * architecture. Instability is computed
 * as the ratio of afferent and efferent
 * coupling between components.
 * 
 * <p>From: 
 * "On the Modularity of Software Architectures:
 * A Concern-Driven Measurement Framework" (2007)
 * 
 * <p>http://www.ndepend.com/Metrics.aspx#MetricsOnAssemblies
 * 
 * <p>History:
 * <ul>
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * */
public class Instability extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2806277382769987083L;

	/** Number of afferent relations for each component */
	private boolean [][] afferentRelations;

	/** Number of efferent relations for each component */
	private boolean [][] efferentRelations;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public Instability(){
		super();
		setName("ins");
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// Get genotype
		SyntaxTree genotype = ind.getGenotype();
		int numOfComponents = ind.getNumberOfComponents();

		int j, actualCmp=-1, otherCmp=-1;
		boolean isInterface = false, isOtherInterface = false, isConnector = false;
		String symbol, otherSymbol, class1, class2, otherClass1, otherClass2, otherInterface;
		boolean isRequired, exit;

		// Initialize
		this.afferentRelations = new boolean[numOfComponents][numOfComponents];
		this.efferentRelations = new boolean[numOfComponents][numOfComponents];

		for(int i=0; i<numOfComponents; i++){
			for(int k=0; k<numOfComponents; k++){
				this.afferentRelations[i][k] = false;
				this.efferentRelations[i][k] = false;
			}
		}

		// Compute needed metrics for each component
		for(int i=1; !isConnector; i++){

			symbol = genotype.getNode(i).getSymbol();

			// Non terminal node
			if(genotype.getNode(i).arity()!=0){
				// The symbol classes indicates the beginning of a new component
				if(symbol.equalsIgnoreCase("component")){
					isInterface=false;
					actualCmp++;
				}
				else if(symbol.equalsIgnoreCase("required-interfaces")){
					isInterface=true;
				}
				else if(symbol.equalsIgnoreCase("connectors")){
					isConnector=true;
				}
			}

			// Terminal node
			else{
				// If the terminal is an interface
				if(isInterface){

					// Split the names of the classes involved in the interaction
					String [] aux = symbol.split("_");
					class1 = aux[0];
					class2 = aux[2];

					// Set if it is a required or provided interface 
					if(aux[1].equalsIgnoreCase("req"))
						isRequired=true;
					else
						isRequired=false;

					// Search the other interface
					otherCmp=actualCmp;			// Start in the next component
					j=i+1;
					isOtherInterface=false;
					exit=false;
					while(!exit && !(genotype.getNode(j).getSymbol().equalsIgnoreCase("connectors"))){

						otherSymbol = genotype.getNode(j-1).getSymbol();

						if(genotype.getNode(j).getSymbol().equalsIgnoreCase("component")){
							otherCmp++;
						}

						// The original interface is a required interface and 
						// we reach a new set of provided interfaces or the 
						// original interface is provided interface and 
						// we reach a new set of required interfaces
						if((isRequired && otherSymbol.equalsIgnoreCase("provided-interfaces")) || (!isRequired && otherSymbol.equalsIgnoreCase("required-interfaces"))){
							isOtherInterface = true;
						}

						// The end of a set of terminal nodes
						if(genotype.getNode(j).arity()!=0){
							isOtherInterface = false;
						}

						// Another interface has found, check it
						if(isOtherInterface && actualCmp!=otherCmp){
							otherInterface = genotype.getNode(j).getSymbol();
							//System.out.println("otherInterface: " + otherInterface);
							String [] aux2 = otherInterface.split("_");
							otherClass1 = aux2[0];
							otherClass2 = aux2[2];

							//System.out.println("\tComponente: " + otherCmp + " Interfaz: " + otherInterface + " clase1: " + otherClass1 + " clase2: " + otherClass2);

							// Check if it is the pair interface in the other component
							if(class1.equalsIgnoreCase(otherClass2) && class2.equalsIgnoreCase(otherClass1)){

								// If the interface is required by the original component,
								// increment the number of efferent relations in the original component
								// and the number of afferent relation in the other component.
								if(isRequired){
									this.efferentRelations[actualCmp][otherCmp]=true;
									this.afferentRelations[otherCmp][actualCmp]=true;
								}

								// If the interface is provided by the original component,
								// increment the number of afferent relations in the original component
								// and the number of efferent relation in the other component.
								else{
									this.afferentRelations[actualCmp][otherCmp]=true;
									this.efferentRelations[otherCmp][actualCmp]=true;
								}
								// In this version, a provided interfaces matches with an unique required interface and vice versa
								exit=true;
							}
						}
						j++;
					}
				}
			}
		}// end of tree route
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		int numOfComponents = ind.getNumberOfComponents();
		int numAfferent, numEfferent;
		double avg=0.0;

		if(ind.isFeasible()){
			// Compute instability of each component
			for(int i=0; i<numOfComponents; i++){
				numAfferent=0;
				numEfferent=0;
				// Get the total number of afferent and efferent relations
				for(int j=0; j<numOfComponents; j++){
					if(afferentRelations[i][j])
						numAfferent++;
					if(efferentRelations[i][j])
						numEfferent++;
				}

				avg += ((double)numEfferent)/((double)(numAfferent+numEfferent));

			}

			// Average instability in the architecture
			avg /= numOfComponents;
		}
		else
			avg = Double.POSITIVE_INFINITY;
		return new SimpleValueFitness(avg);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(1.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}