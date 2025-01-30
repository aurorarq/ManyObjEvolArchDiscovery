package net.sf.jclec.sbse.cl2cmp.mo.exp;

public class ShowCombinations {

	private static String metrics [] = {"icd","erp","gcr","cs","cl","ins","enc","abs","cb","ib"};

	
	public ShowCombinations() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Combinaciones de 2 objetivos");
		int n=1;
		for(int i=0; i<metrics.length; i++){
			for(int j=i+1; j<metrics.length; j++){
				System.out.println(metrics[i] + " - " + metrics[j]);
				n++;
			}
		}
		System.out.println("total: " + (n-1));
		
		System.out.println("Combinaciones de 4 objetivos");
		n=1;
		for(int i=0; i<metrics.length; i++){
			for(int j=i+1; j<metrics.length; j++){
				for(int k=j+1; k<metrics.length; k++){
					for(int l=k+1; l<metrics.length; l++){
						System.out.println(metrics[i] + " - " + metrics[j] + " - " + metrics[k] + " - " + metrics[l]);
						n++;
					}
				}
			}
		}
		System.out.println("total: " + (n-1));
		
		System.out.println("Combinaciones de 6 objetivos");
		n=1;
		for(int i=0; i<metrics.length; i++){
			for(int j=i+1; j<metrics.length; j++){
				for(int k=j+1; k<metrics.length; k++){
					for(int l=k+1; l<metrics.length; l++){
						for(int m=l+1; m<metrics.length; m++){
							for(int r=m+1; r<metrics.length; r++){
								System.out.println(metrics[i] + " - " + metrics[j] + " - " + metrics[k] 
										+ " - " + metrics[l] + " - " + metrics[m] + " - " + metrics[r]);
								n++;
							}
						}
					}
				}
			}
		}
		System.out.println("total: " + (n-1));
		
		System.out.println("Combinaciones de 8 objetivos");
		n=1;
		for(int i=0; i<metrics.length; i++){
			for(int j=i+1; j<metrics.length; j++){
				for(int k=j+1; k<metrics.length; k++){
					for(int l=k+1; l<metrics.length; l++){
						for(int m=l+1; m<metrics.length; m++){
							for(int r=m+1; r<metrics.length; r++){
								for(int s=r+1; s<metrics.length; s++){
									for(int t=s+1; t<metrics.length; t++){
										System.out.println(metrics[i] + " - " + metrics[j] + " - " + metrics[k] + " - " + metrics[l] 
												+ " - " + metrics[m] + " - " + metrics[r] + " - " + metrics[s] + " - " + metrics[t]);
										n++;
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("total: " + (n-1));
	}
}
