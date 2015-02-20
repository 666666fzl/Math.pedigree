package samePedigree;
import baseModel.FamilyTree;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class SamePedigreeProb {
	static public double[] probabilityBackward;
	static public double[] probabilityForward;

	static private FamilyTree pedigreeBackward;
	static private PedigreeTree pedigreeForward;
	static private int idA;
	static private int idB;

	public SamePedigreeProb(int N, int G, int cTree) {

		for (int j = 0; j < cTree; j++) {
			int idA=(int)(Math.random()*N*2);
			int idB=(int)(Math.random()*N*2);;
			while(idA==idB)
				idB=(int)(Math.random()*N*2);
			this.idA=idA;this.idB=idB;
			pedigreeBackward = new FamilyTree(N, G,idA, idB);

			pedigreeForward=new PedigreeTree(G,N,pedigreeBackward);
			//pedigreeForward.printPedigree(idA, idB, G);
		}

	}
	private void generatePath(int cPath, int N, int cTree)
	{
		probabilityBackward=new double[15];
		probabilityForward=new double[15];
		int status=0;
		for (int i = 0; i < cPath; i++){
			pedigreeBackward.checkInheritance();

			pedigreeForward.setInheritance();

			pedigreeForward.clearMap();
			for(int gene=1;gene<=4*N;gene++){
				pedigreeForward.DFSsearch(1,gene, gene);
			}
			status=pedigreeForward.IBDstatus(idA+1, idB+1);
			probabilityForward[status-1]++;

		}
		setProb(pedigreeBackward.getProb(cPath));
		for (int i = 0; i < 15; i++){
			probabilityBackward[i] = (double) (probabilityBackward[i] /(double) cTree);
			probabilityForward[i]=probabilityForward[i]*1.0/cPath/cTree;
		}
		pedigreeBackward.clearIBD();
	}
	private void setProb(double[] from) {
		for (int i = 0; i < 15; i++) {
			probabilityBackward[i] += from[i];
		}
	}

	public static void main(String[] args) {
		int N=10; 
		int G=10; 
		int cPedig=1; 
		int cInheri=200;

		SamePedigreeProb probs=new SamePedigreeProb(N, G, cPedig);
		
		double aAB=pedigreeBackward.getKinship(pedigreeBackward.getA(),pedigreeBackward.getB(),2*N);
		double aAA=pedigreeBackward.getKinship(pedigreeBackward.getA(),pedigreeBackward.getA(),2*N);
		double aBB=pedigreeBackward.getKinship(pedigreeBackward.getB(),pedigreeBackward.getB(),2*N);
		//System.out.println(aAB+" "+aAA+" "+aBB);
		double AB=pedigreeForward.Kinship(idA,idB);
		double AA=pedigreeForward.Kinship(idA,idA);
		double BB=pedigreeForward.Kinship(idB,idB);
		//System.out.println(AB+" "+AA+" "+BB);
		if ((aAB+aAA+aBB)!=(AB+AA+BB)){
			System.out.println("error");
		}
		
		double[] n2= {0,1,1,0,0,1,1,2,2,2,2,0,2,2,4};// # of outbreeding edges between A, B for each identity state
		double[] n1= {0,0,0,1,0,0,0,1,1,0,0,1,0,0,1};// # of outbreeding edges between A, A for each identity state 
		double[] n3= {0,0,0,0,1,0,0,0,0,1,1,1,0,0,1};// # of outbreeding edges between B, B for each identity state 
		double totalBack=0, totalFor=0,sum=0;
		
		for (int iteration=0;iteration<10;iteration++){
			probs.generatePath( cInheri, N, cPedig);
			System.out.println("path: "+cInheri);
			
			double backKinshipAA=0, backKinshipAB=0, backKinshipBB=0;
			for(int i=0; i<15;i++){
				backKinshipAB+=n2[i]*probs.probabilityBackward[i]/4.0;
				backKinshipAA+=n1[i]*probs.probabilityBackward[i]/1.0;
				backKinshipBB+=n3[i]*probs.probabilityBackward[i]/1.0;

			}

			backKinshipAA=(backKinshipAA+1)/2;
			backKinshipBB=(backKinshipBB+1)/2;
			
		
			sum=0;
			sum+=Math.abs(backKinshipAB-aAB);
			sum+=Math.abs(backKinshipAA-aAA);
			sum+=Math.abs(backKinshipBB-aBB);
			
			System.out.println("back "+sum/(aAB+aAA+aBB));
			
			double forwardKinshipAA=0, forwardKinshipAB=0, forwardKinshipBB=0;

			for(int i=0; i<15;i++){
				forwardKinshipAB+=n2[i]*probs.probabilityForward[i]/4.0;
				forwardKinshipAA+=n1[i]*probs.probabilityForward[i]/1.0;
				forwardKinshipBB+=n3[i]*probs.probabilityForward[i]/1.0;

			}

			forwardKinshipAA=(forwardKinshipAA+1)/2;
			forwardKinshipBB=(forwardKinshipBB+1)/2;
			
			
			sum=0;
			sum+=Math.abs(forwardKinshipAB-AB);
			sum+=Math.abs(forwardKinshipAA-AA);
			sum+=Math.abs(forwardKinshipBB-BB);
			
			System.out.println("for "+sum/(AB+AA+BB));
			
			cInheri+=1000*iteration;
		}


		/*
		sum=0;
		sum+=Math.abs(forwardKinshipAB-backKinshipAB);
		sum+=Math.abs(forwardKinshipAA-backKinshipAA);
		sum+=Math.abs(forwardKinshipBB-backKinshipBB);
		System.out.println("forward backward actual difference");
		System.out.println("Error AB : "+Math.abs(forwardKinshipAB-backKinshipAB));
		System.out.println("Error AA : "+Math.abs(forwardKinshipAA-backKinshipAA));
		System.out.println("Error BB : "+Math.abs(forwardKinshipBB-backKinshipBB));
		System.out.print(sum+" ");	
		System.out.println();
		
		sum=0;
		sum+=Math.abs(AB-backKinshipAB);
		sum+=Math.abs(AA-backKinshipAA);
		sum+=Math.abs(BB-backKinshipBB);
		System.out.println("actual backward and estimate forward difference");
		System.out.println("Error AB : "+Math.abs(AB-backKinshipAB));
		System.out.println("Error AA : "+Math.abs(AA-backKinshipAA));
		System.out.println("Error BB : "+Math.abs(BB-backKinshipBB));
		System.out.print(sum+" ");	
		System.out.println();*/

	}

}
