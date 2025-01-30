package net.sf.jclec.sbse.cl2cmp.mo.exp;

public class XMLTags {

	public static String objTags [] = {
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.ICD\" maximize=\"true\"/>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.InvERP\"  maximize=\"true\">\n<assoc-weight>1.0</assoc-weight>\n<aggreg-weight>2.0</aggreg-weight>"+
		"\n<compos-weight>3.0</compos-weight>\n<gener-weight>5.0</gener-weight>\n</objective>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.InvGCR\" maximize=\"true\"/>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.InvInstability\" maximize=\"true\"/>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.InvCritSize\" maximize=\"true\">\n<size-threshold>0.3</size-threshold>\n</objective>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.Encapsulation\" maximize=\"true\"/>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.InvCritLink\" maximize=\"true\">\n<link-threshold>8</link-threshold>\n</objective>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.ComponentBalance\" maximize=\"true\"/>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.InterfaceBalance\" maximize=\"true\"/>",
		"\t\t\t\t<objective type=\"net.sf.jclec.sbse.cl2cmp.mo.objectives.Abstractness\" maximize=\"true\"/>"
	};
	
	public static String epsilonTags [] = {
		"\t\t\t\t\t<epsilon-value>0.05</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>5</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>0.1</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>0.05</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>1</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>0.05</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>1</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>0.05</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>0.05</epsilon-value>",
		"\t\t\t\t\t<epsilon-value>0.05</epsilon-value>"
	};
	
	public static String evaluatorTag = "\t\t\t\t</epsilon-values>\n\t\t\t</mo-evol-strategy>\n\t\t</mo-evol-strategy>\n" +
			"\t\t<evaluator type=\"net.sf.jclec.sbse.cl2cmp.mo.Cl2CmpMoEvaluator\">\n\t\t\t<objectives>";

	
	public static String endEvaluatorTags = "\t\t\t</objectives>\n\t\t</evaluator>";
	public static String endTags = "\t</process>\n</experiment>";
}