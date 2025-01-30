package net.sf.jclec.sbse.cl2cmp.mo;

import net.sf.jclec.sbse.cl2cmp.Cl2CmpSpecies;
import net.sf.jclec.syntaxtree.SyntaxTree;
import net.sf.jclec.syntaxtree.SyntaxTreeIndividual;

/**
 * Species for 'Classes to Components' (Cl2Cmp) problem
 * formulated as a multi-objective problem.
 * 
 * @author Aurora Ramirez Quesada
 * @author Jose Raul Romero
 * @author Sebastian Ventura
 * 
 * @version 1.0
 * History:
 * <ul>
 * 	<li>1.0: Creation (December 2013)
 * </ul>
 * */
public class Cl2CmpMoSpecies extends Cl2CmpSpecies {

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------------- Properties
	//////////////////////////////////////////////////////////////////
	
	/** Serial ID */
	private static final long serialVersionUID = 6297845260447280385L;

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	@Override
	public SyntaxTreeIndividual createIndividual(SyntaxTree genotype) {
		return new Cl2CmpMoIndividual(genotype);
	}
}
