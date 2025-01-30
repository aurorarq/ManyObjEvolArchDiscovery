package net.sf.jclec.sbse.cl2cmp.mo.algorithms;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.IValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;

public class eProgressFitnessComparator extends ValueFitnessComparator{

	double epsilon = 0.0;
	
	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
	
	public eProgressFitnessComparator() {
		super();
	}
	
	public int compare(IFitness fitness1, IFitness fitness2) 
	{
		double f1value, f2value;  
        try {
        	f1value = ((IValueFitness) fitness1).getValue();
        }
        catch(ClassCastException e) {
            throw new IllegalArgumentException
            	("IValueFitness expected in fitness1");
        }
        try {
            f2value = ((IValueFitness) fitness2).getValue();
        }
        catch(ClassCastException e) {
            throw new IllegalArgumentException
            	("IValueFitness expected in fitness2");
        }
        if (f1value > (f2value+epsilon)) {
            return inverse ? -1 : 1;
        }
        else if(f1value < (f2value+epsilon)) {
            return inverse ? 1 : -1;
        }
        else {
            return 0;
        }
	}
}
