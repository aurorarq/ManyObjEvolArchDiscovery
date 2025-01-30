package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import java.util.ArrayList;

import es.uco.kdis.datapro.dataset.Column.MultivalueColumn;
import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.syntaxtree.SyntaxTree;
import net.sf.jclec.util.range.Closure;

/**
 * Intra-Modular Coupling Density (ICD) Metric
 * inspired by "Optimization Model of COTS Selection Based 
 * on Cohesion and Coupling for Modular Software Systems 
 * under Multiple Applications Environment" (2012)
 * 
 * <p>History:
 * <ul>
 * 	<li>2.0: Now extending Cl2CmpObjective
 * 	<li>1.0: Creation (September 2013)
 *  <li>1.1: Adaptation to EMO algorithms design (December 2013)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 2.0
 * */
public class ICD extends Cl2CmpObjective {

	/** Serial ID */
	private static final long serialVersionUID = -2151015582839455136L;

	/** Internal relations for each component */
	protected double c_in [];

	/** External relations for each component */
	protected double c_out [];

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public ICD(){
		super();
		setName("icd");
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// Get genotype
		SyntaxTree genotype = ind.getGenotype();

		int numberOfComponents = ind.getNumberOfComponents();

		int j, otherClassIndex, actualCmp=-1;
		boolean isClass = false, isConnector = false;
		MultivalueColumn column, otherColumn;
		String symbol;

		// Initialize
		this.c_in = new double [numberOfComponents];
		this.c_out = new double [numberOfComponents];

		for(int i=0; i<numberOfComponents; i++){
			this.c_in[i] = this.c_out[i] = 0;
		}

		// Compute c_in for each component in the individual
		for(int i=1; !isConnector; i++){

			symbol = genotype.getNode(i).getSymbol();

			// Non terminal node
			if(genotype.getNode(i).arity()!=0){
				// The symbol classes indicates the beginning of a 
				// group of classes in a component
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

				// Get the dataset information about the class
				column = (MultivalueColumn) this.dataset.getColumnByName(symbol);

				// Check the relations with the rest of class in the component
				j=i+1;	
				while(genotype.getNode(j).arity()==0){
					otherColumn = (MultivalueColumn)this.dataset.getColumnByName(genotype.getNode(j).getSymbol());
					otherClassIndex = this.dataset.getIndexOfColumn(otherColumn);

					ArrayList<Object> relations = (ArrayList<Object>) column.getMultiElement(otherClassIndex);

					// Not an invalid value
					if(relations.size()>1){
						this.c_in[actualCmp] += ((double)relations.size()/2);
					}
					j++;
				}
			}
		}// end of tree route

		// Compute c_out
		for(int i=0; i<numberOfComponents; i++){
			this.c_out[i] = ind.getNumberOfProvided(i) + ind.getNumberOfRequired(i);
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		// Weighted ICD
		double icd_i, classesRatio;
		double nComponents = c_in.length;
		double nClasses = 0.0;
		double result = 0.0;

		if(ind.isFeasible()){
			// total number of classes
			for(int i=0; i<nComponents; i++){
				nClasses += ind.getNumberOfClasses(i);
			}

			for(int i=0; i<this.c_in.length; i++){
				if(this.c_in[i]!=0 || this.c_out[i]!=0){
					icd_i = this.c_in[i]/(this.c_in[i]+this.c_out[i]);
					classesRatio = (nClasses - ind.getNumberOfClasses(i))/nClasses;
					result += icd_i*classesRatio;
				}
			}
			result = result/nComponents;
		}
		else
			result = Double.NEGATIVE_INFINITY;
		return new SimpleValueFitness(result);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(1.0);
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
}
