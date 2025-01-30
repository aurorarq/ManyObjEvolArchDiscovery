package net.sf.jclec.sbse.cl2cmp.mo.objectives;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import es.uco.kdis.datapro.dataset.Column.ColumnAbstraction;
import es.uco.kdis.datapro.dataset.Column.MultivalueColumn;
import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoIndividual;
import net.sf.jclec.syntaxtree.SyntaxTree;
import net.sf.jclec.util.range.Closure;

/**
 * External Relations Penalty (ERP) Metric
 * 
 * <p>History:
 * <ul>
 * <li>2.0: Now extending Cl2CmpObjective (June 2014)
 * <li>1.1: Adaptation to EMO algorithms design (December 2013)
 * <li>1.0: Creation (September 2013)
 * </ul>
 * 
 * @author Aurora Ramirez
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * @version 2.0
 * */
public class ERP extends Cl2CmpObjective {
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8281899910405666250L;


	/** Number of external connections for each component */
	private int componentNumberExternalConnections [];

	/** Max relation weight between pairs of components */
	private double componentMaxWeightedExternalConnections [][];

	/** Total relations weights between pairs of components */
	private double componentSumWeightedExternalConnections [][];

	/** UML relation weights */
	protected double umlWeights [];

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public ERP(){
		super();
		setName("erp");
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for ERP are:
	 * <ul>
	 * 	<li>uml-relation-weights (Complex): 
	 * 	Associated weight to each UML relationship:
	 * 		<ul>
	 * 			<li>assoc-weight (<code>double</code>): 
	 * 			Weight for associations. Default value is 1.0.
	 * 			<li>aggreg-weight (<code>double</code>): 
	 * 			Weight for aggregations. Default value is 1.0.
	 * 			<li>compos-weight (<code>double</code>): 
	 * 			Weight for compositions. Default value is 1.0.
	 * 			<li>gener-weight (<code>double</code>): 
	 * 			Weight for generalizations. Default value is 1.0.
	 * 		</ul>
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {
		
		super.configure(settings);
		
		// Configure weights for UML relations
		this.umlWeights = new double[4];
		String names [] = new String[]{"assoc", "aggreg", "compos", "gener"};
		Object property;
		for(int i=0; i<4; i++){
			property = settings.getProperty("objective("+getIndex()+")."+names[i]+"-weight");
			if(property == null){
				this.umlWeights[i] = 1.0;
			}
			else{
				try{
					this.umlWeights[i] = Double.parseDouble(property.toString());
					if(this.umlWeights[i] < 0.0){
						throw new IllegalArgumentException("The " + names[i] + " weight must be greater than 0");
					}
				}catch(NumberFormatException e){
					throw new IllegalArgumentException("The " + names[i] + " weight must be a number");
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void prepare(Cl2CmpMoIndividual ind) {
		// Get genotype
		SyntaxTree genotype = ind.getGenotype();
		int numberOfComponents = ind.getNumberOfComponents();

		int j, actualIndex, classIndex, actualCmp=-1, otherCmp=-1;
		boolean isClass = false, isOtherClass = false, isConnector = false;
		MultivalueColumn column;
		int nav_ij, nav_ji, relationType;
		String symbol;

		// Initialize
		this.componentNumberExternalConnections = new int[numberOfComponents];
		this.componentMaxWeightedExternalConnections = new double [numberOfComponents][numberOfComponents];
		this.componentSumWeightedExternalConnections = new double [numberOfComponents][numberOfComponents];

		for(int i=0; i<numberOfComponents; i++){
			this.componentNumberExternalConnections[i] = 0;
			for(j=0; j<numberOfComponents; j++){
				this.componentMaxWeightedExternalConnections[i][j] = 0.0;
				this.componentSumWeightedExternalConnections[i][j] = 0.0;
			}
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
					column = (MultivalueColumn) this.dataset.getColumnByName(symbol);
					actualIndex = this.dataset.getIndexOfColumn(column);

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
							classIndex = this.dataset.getIndexOfColumn(this.dataset.getColumnByName(genotype.getNode(j).getSymbol()));
							ArrayList<Object> relations1 = (ArrayList<Object>) column.getMultiElement(classIndex);

							// Not an invalid value
							if(relations1.size()>1){

								ArrayList<Object> relations2 = (ArrayList<Object>)((MultivalueColumn)this.dataset.getColumn(classIndex)).getMultiElement(actualIndex);

								// Check type and navigation of each relation
								for(int k=1; k<relations1.size(); k+=2){

									relationType = Integer.parseInt((String)relations1.get(k-1));
									nav_ij = Integer.parseInt((String)relations1.get(k));
									nav_ji = Integer.parseInt((String)relations2.get(k));

									// Not a candidate interface, because its a bidirectional relation
									if(nav_ij==nav_ji){
										this.componentNumberExternalConnections[actualCmp]++;
										this.componentNumberExternalConnections[otherCmp]++;

										// Check the type of relation (dependences are not possible)
										double sumTerm = 0.0;
										switch(relationType){
										case 1: sumTerm = this.umlWeights[0]; break; // Association
										case 3: sumTerm = this.umlWeights[1]; break; // Aggregation
										case 4: sumTerm = this.umlWeights[2]; break; // Composition	
										case 5: sumTerm = this.umlWeights[3]; break; // Generalization
										}

										// Update total sum
										this.componentSumWeightedExternalConnections[actualCmp][otherCmp] += sumTerm;
										this.componentSumWeightedExternalConnections[otherCmp][actualCmp] += sumTerm;

										// Update max value if necessary
										if(this.componentMaxWeightedExternalConnections[actualCmp][otherCmp] < sumTerm){
											this.componentMaxWeightedExternalConnections[actualCmp][otherCmp] = sumTerm;
											this.componentMaxWeightedExternalConnections[otherCmp][actualCmp] = sumTerm;
										}
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
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected IFitness compute(Cl2CmpMoIndividual ind) {
		double numberOfComponents = ind.getNumberOfComponents();
		double result = 0.0;

		// Total weighted external relations
		for(int i=0; i<numberOfComponents; i++)
			for(int j=i; j<numberOfComponents; j++)
				result += this.componentSumWeightedExternalConnections[i][j];
		return new SimpleValueFitness(result);
	}

	@Override
	public void computeBounds() {
		this.getInterval().setLeft(0.0);
		this.getInterval().setRight(computeMaxValue());
		this.getInterval().setClosure(Closure.ClosedClosed);
	}
	
	/**
	 * Calculate the minimum value using
	 * the information in the dataset. It
	 * sums the weight of each possible external
	 * relation (those relationships which does
	 * not explicitly specifies its navigability)
	 * */
	protected double computeMaxValue(){
		List<Object> values, values2;
		List<ColumnAbstraction> cols = dataset.getColumns();
		int totalRel = 0;
		int size = cols.size(), size2;
		MultivalueColumn col;
		int type, nav_ij, nav_ji;
		for(int i=0; i<size; i++){
			col = (MultivalueColumn) cols.get(i);
			size2 = col.getSize();
			for(int j=i; j<size2; j++){
				values = col.getMultiElement(j);
				values2 = ((MultivalueColumn)this.dataset.getColumn(j)).getMultiElement(i);
				if(values.size()>1){//Not an invalid value

					for(int k=0; k<values.size(); k+=2){
						// Get the type of relationship
						type = Integer.parseInt((String)values.get(k));
						nav_ij = Integer.parseInt((String)values.get(k+1));
						nav_ji = Integer.parseInt((String)values2.get(k+1));

						// Not a candidate interface, because its a bidirectional relation
						if(nav_ij==nav_ji){
							// Add the correspondent weight to the total sum
							switch(type){
							case 1: totalRel += this.umlWeights[0]; break; // Association
							case 3: totalRel += this.umlWeights[1]; break; // Aggregation
							case 4: totalRel += this.umlWeights[2]; break; // Composition	
							case 5: totalRel += this.umlWeights[3]; break; // Generalization
							}
						}
					}
				}
			}
		}
		// Total sum of weighted relationships
		return (double)totalRel;
	}
}