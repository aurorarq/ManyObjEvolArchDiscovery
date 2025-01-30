package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.syntaxtree.SyntaxTree;
import net.sf.jclec.util.range.Closure;

/**
 * Encapsulation Metric. Maximize the average
 * encapsulation of the components in the
 * architecture. Encapsulation is measured
 * as the ratio of internal classes and
 * total number of classes inside a component.
 * 
 * Inspired by Data Access Metric defined in: 
 * "A hierarchical model for object-oriented 
 * design quality assessment" (2002)
 * 
 * <p>History:
 * <ul>
 * 	<li>2.0: Now extending Cl2CmpObjective
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 1.0
 * @see Cl2CmpObjective
 * */

public class Encapsulation extends Cl2CmpObjective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -867632604415990554L;

	/** Number of internal classes in each component */
	private int [] internalClasses;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public Encapsulation(){
		super();
		setName("en");
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// Get genotype
		SyntaxTree genotype = ind.getGenotype();
		int numOfComponents = ind.getNumberOfComponents();

		int j, actualCmp=-1;
		boolean isClass = false, isConnector = false;
		String symbol, otherSymbol;
		boolean isInternal = true, isSameComponent, isInterface;

		// Initialize
		this.internalClasses = new int[numOfComponents];
		for(int i=0; i<numOfComponents; i++){
			this.internalClasses[i] = 0;
		}

		// Compute needed metrics for each component
		for(int i=1; !isConnector; i++){

			symbol = genotype.getNode(i).getSymbol();

			// Non terminal node
			if(genotype.getNode(i).arity()!=0){
				// The symbol classes indicates the beginning of a new set of classes
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

					// Search the interface definition in the component
					j=i+1;
					isInternal = true;
					isSameComponent = true;
					isInterface = false;

					while(isInternal && isSameComponent){

						otherSymbol = genotype.getNode(j).getSymbol();

						// Check if it is the end of the current component
						if(otherSymbol.equalsIgnoreCase("component")
								|| otherSymbol.equalsIgnoreCase("connectors")){
							isSameComponent = false;
							isInterface = false;
						}
						// Check if the current node represents the first interface
						if(genotype.getNode(j-1).getSymbol().contains("interfaces") &&
								genotype.getNode(j).arity()==0){
							isInterface = true;
						}

						// If the current node is an interface, check if it is implemented
						// by the class, then, the class is not internal
						if(isInterface && otherSymbol.contains(symbol)){
							isInternal = false;
						}

						j++;
					}

					// If the class is internal, increment the component counter
					if(isInternal){
						this.internalClasses[actualCmp]++;
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
		double avg = 0.0;
		for(int i=0; i<numOfComponents; i++){
			avg += ((double)this.internalClasses[i])/((double)ind.getNumberOfClasses(i));
		}
		avg /= numOfComponents;
		return new SimpleValueFitness(avg);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(1.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
