package samePedigree;
import baseModel.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class PedigreeTree {
	public location[][] pedigree;//pedigree
	private int[][] minheritance;//mother's inheritance path
	private int[][] finheritance;//father's inheritance path
	private int[][] alleleMap;
	private int G;
	private int N;
	/**
	 * Constructor to randomly create a pedigree with inheritance path:G*2N*4
	 * @param G
	 * @param N
	 * @param tree
	 */
	public PedigreeTree(int G, int N,FamilyTree tree){
		this.G= G;
		this.N= N;
		setPedigree(tree);

	}
	/**
	 * initialize the pedigree by simulating the family tree
	 * mum from N+1 to 2N and dad from 1 to N
	 * @param tree
	 */
	public void setPedigree(FamilyTree tree){
		this.pedigree= new location[this.G][2*this.N];
		for(int j=0; j<2*N; j++){
			pedigree[0][j]=new location(0,j+1,j+1);
		}
		for(int i=1;i<G;i++){
			for(int j=0; j<2*N; j++){
				pedigree[i][j]=new location(i,(int)(Math.random()*N+N+1),(int)(Math.random()*N+1));
			}
		}
		simulateTree(tree);
	}
	/**
	 * make the pedigree in matrix has the same structure as tree generated in backward algorithm
	 * @param tree
	 */
	private void simulateTree(FamilyTree tree)
	{
		Queue<Node> people=new LinkedList<Node>();
		people.add(tree.getA());people.add(tree.getB());
		while(!people.isEmpty())
		{
			Node curr=people.poll();
			if(curr.getFather()==null)
				break;
			int i=curr.getG();int j=curr.getId();
			pedigree[i][j].setParent(curr.getMotherId()+1, curr.getFatherId()+1);
			people.add(curr.getMother());
			people.add(curr.getFather());
		}
	}
	/**
	 * print the pedigree, used for debug
	 * @param idA
	 * @param idB
	 * @param G
	 */
	public void printPedigree(int idA, int idB, int G)
	{
		System.out.println("forward family pedigree");
		Queue<Integer> childrenIndex=new LinkedList<Integer>();
		childrenIndex.add(idA); childrenIndex.add(idB);
		
		while(G>1){
			G--;
			Queue<Integer> parentIndex=new LinkedList<Integer>();
			while(!childrenIndex.isEmpty()){
				int curr=childrenIndex.poll();
				int mum=pedigree[G][curr].getMi()-1;
				int dad=pedigree[G][curr].getFi()-1;
				parentIndex.add(mum);parentIndex.add(dad);
				System.out.println(curr+ " | "+pedigree[G][curr].getG()+" : mother "+mum+" father "+dad);
			}
			System.out.println();
			childrenIndex=parentIndex;
		}
	}
	
	/**
	 * copy constructor
	 * @param mpedigree
	 * @param fpedigree
	 * @param minheritance
	 * @param finheritance
	 */
	public PedigreeTree(location[][] mpedigree, int[][] minheritance, int[][] finheritance){
		this.pedigree= mpedigree;
		this.minheritance= minheritance;
		this.finheritance= finheritance;
		this.G= mpedigree.length;
		this.N= mpedigree[0].length/2;
		this.alleleMap=new int[G][4*N];
		for(int i=0; i<4*N; i++){
			alleleMap[0][i]=i+1;
		}

	}
	public void clearMap(){
		this.alleleMap=new int[G][4*N];
		for(int i=0; i<4*N; i++){
			alleleMap[0][i]=i+1;
		}
	}
	public void setInheritance(){
		this.minheritance= new int[G][2*N];
		this.finheritance= new int[G][2*N];
		for(int i=1;i<G;i++){
			for(int j=0; j<2*N; j++){
				minheritance[i][j]=(int)(Math.random()*2);//0:left; 1:right
				finheritance[i][j]=(int)(Math.random()*2);//0:left; 1:right
			}
		}

	}

	public location[][] getfpedigree(){
		return this.pedigree;
	}



	public int[][] getfinheritance(){
		return this.finheritance;
	}

	public int[][] getminheritance(){
		return this.minheritance;
	}
	public int[][] getalleleMap(){
		return this.alleleMap;
	}
	public int getG(){
		return this.G;
	}
	public int getN(){
		return this.N;
	}


	/**
	 * Returns an allelMap based on the pedigree and inheritence path
	 * @param g - generation
	 * @param index- index of allele that is inherited
	 * @param allele- index of allele at the top generation (1 to 4N)
	 */
	public void DFSsearch(int g, int index, int allele){
		int pindex=index/2+index%2;//the corresponding person's index of the allele checked
		if(pindex <= N){//male->check father's index and allele index
			for(int i=0; i<4*N; i=i+2){
				if(alleleMap[g][i]==0){
					int person=i/2;
					if((pedigree[g][person].getFi()==pindex) && (finheritance[g][person]==(index+1)%2)){
						alleleMap[g][i]=allele;
						if(g<G-1) DFSsearch(g+1, i+1, allele);
					}
				}		
			}
		}
		else{
			for(int i=1; i<4*N; i=i+2){
				if(alleleMap[g][i]==0){
					int person=i/2+1;
					if(pedigree[g][person-1].getMi()==pindex&&minheritance[g][person-1]==(index+1)%2){
						alleleMap[g][i]=allele;
						if(g<G-1) DFSsearch(g+1, i+1, allele);
					}
				}		
			}
		}
	}
	/**
	 * 
	 * @param x index of selected person 1
	 * @param y index of selected person 2
	 * @return the corresponding IBD status
	 */
	public int IBDstatus(int x, int y){
		//System.out.println(x+" "+y);
		int A1=alleleMap[G-1][2*x-1-1];
		int	A2=alleleMap[G-1][2*x-1];
		int B1=alleleMap[G-1][2*y-1-1];
		int B2=alleleMap[G-1][2*y-1];
		if(A1==A2){
			if((A1==B1)&&(A1==B2))
				return 15;
			else if(A1==B1)
				return 9;
			else if(A1==B2)
				return 8;
			else if(B1==B2)
				return 12;
			else
				return 4;
		}
		else if(A1==B1){
			if(A1==B2)
				return 10;
			else if(A2==B2)
				return 13;
			else
				return 2;
		}
		else if(A1==B2){
			if(A2==B1)
				return 14;
			else
				return 6;	
		}
		else if(A2==B2){
			if(B1==B2)
				return 11;
			else
				return 3;
		}
		else if(A2==B1){
			return 7;
		}
		else if(B1==B2){
			return 5;
		}
		else{
			return 1;
		}

	}

	/**
	 * Kinship from the founder generation
	 */
	public double fKin(int i, int j){
		if(i==j)
			return 0.5;
		else 
			return 0;
	}

	/**
	 * debug code, print the table
	 */
	public static void pt(double[][] table){
		for(int i=0; i<table.length; i++){
			for(int j=0; j<table[0].length; j++){
				System.out.print(table[i][j]+" | ");
			}
			System.out.println("\n");
		}
	}

	/**
	 * Print the pedigree matrix
	 */
	public static void pp(location[][] input, int g){
		System.out.println("mother of this generation:");
		for(int i=0; i<input[0].length; i++){
			System.out.print(input[g][i].getMi()+"//");
		}
		System.out.println("\nfather of this generation:");
		for(int i=0; i<input[0].length; i++){
			System.out.print(input[g][i].getFi()+"//");
		}
		System.out.println();
	}

	/**
	 * get the corresponding kinship coefficient from the table
	 * @param i
	 * @param j
	 * @param table
	 * @return
	 */
	public static double getKin(int i, int j, double[][] table){
		return table[i-1][j-1];
	}

	/**
	 * Kinship coefficient calculation:
	 * input: location:a/b
	 * output: kinship of this two location
	 */
	public double Kinship(int a, int b){
		location[][] mat = this.pedigree;	
		int pop =  2*N;
		// kinship matrix: 
		double[][] kin = new double[pop][pop];
		// initialize founder
		for(int i=0; i<pop; i++){			
			for(int j=0; j<pop; j++){
				int m1 = mat[1][i].getMi();
				int f1 = mat[1][i].getFi();
				int m2 = mat[1][j].getMi();
				int f2 = mat[1][j].getFi();
				// same person
				if(i==j)
					kin[i][j] = (1 + fKin(m1, f1) )/2;
				else{
					// no common parent
					kin[i][j] = (fKin(m1, m2)+
							fKin(m1, f2)+
							fKin(f1, m2)+
							fKin(f1, f2))/4;		
				}
			}
		}		
		// compute the whole table:
		for(int i=2; i<G; i++){
			// create a temp table
			double[][] tmp = new double[pop][pop];
			for(int j=0; j<pop; j++){
				for(int k=0; k<pop; k++){
					int m1 = mat[i][j].getMi();
					int f1 = mat[i][j].getFi();
					int m2 = mat[i][k].getMi();
					int f2 = mat[i][k].getFi();
					if(j==k)
						tmp[j][k] = (1+getKin(m1, f1, kin))/2;
					else{
						tmp[j][k] = (getKin(m1, m2, kin)+
								getKin(m1, f2, kin)+
								getKin(f1, m2, kin)+
								getKin(f1, f2, kin))/4;
					}
				}
			}
			kin = tmp;
		}	
		return getKin(a+1, b+1, kin);
	}


}




