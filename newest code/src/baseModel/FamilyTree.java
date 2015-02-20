package baseModel;

import java.util.LinkedList;
/**
 * 
 * @author shuxin, zhencheng
 *
 */
public class FamilyTree {
	private Node A;
	private Node B;
	private  int [] IBD;
	private double [][] kinship;
	public FamilyTree(int N, int G, int idA, int idB)
	{
		IBD=new int[15];
		kinship=new double[G*2*N][G*2*N];
		//create two randomly people
		this.A=new Node(idA,--G);//only need to decrease once for one generation
		this.B=new Node(idB,G);//father
		//father 0~N-1, mother N~(2*N-1)
		LinkedList <Node> children=new LinkedList<Node>();
		children.add(A);
		children.add(B);
		
		buildFamily(children,G,N);
	}
	 /**
	  * get object A 
	  * @return
	  */
	public Node getA()
	{
		return A;
	}
	/**
	 * get object B
	 * @return
	 */
	public Node getB()
	{
		return B;
	}
	/**
	 * use level order traverse to build graph
	 * @param children
	 * @param G
	 * @param N
	 */
	private void buildFamily(LinkedList<Node> children,int G,int N)
	{
		while(G>0)
		{	
			//set a linklist to store all the parents in this generation
			LinkedList<Node> parent=new LinkedList<Node>();
			Node child=children.get(0);//get the first child
			//set up parents
			Node mother=new Node((int)(Math.random()*N+N),--G);//[N,2N)
			parent.add(mother);
			Node father=new Node((int)(Math.random()*N),G);//[0,N)
			parent.add(father);
			child.setParent(mother,father);
			//set up other children's parents
			for(int i=1;i<children.size();i++)
			{
				child=children.get(i);
				Node mama=new Node((int)(Math.random()*N+N),G);//[N,2N)
				Node papa=new Node((int)(Math.random()*N),G);//[0,N)
				checkParent(mama,papa,child,parent);
			}
			
//			for(int i=0;i<children.size();i++)
//				System.out.println(children.get(i).getId()+" | "+children.get(i).getG()+": Mother "+children.get(i).getMother().getId()+" Father "+children.get(i).getFather().getId());
//			System.out.println();
			
			children=parent;
			
		}
	}
	/**
	 * check whether parent nodes have already existed, if they do, then set the child's parents pointers to the previous ones
	 * @param mama
	 * @param papa
	 * @param child
	 * @param parent
	 */
	private void checkParent(Node mama,Node papa,Node child,LinkedList<Node> parent)
	{
		boolean m=false,p=false;
		
		Node myParent=null;
		for(int j=0;j<parent.size();j++)
		{
			myParent=parent.get(j);
			if(mama.equal(myParent))//they are the same person
			{
				child.setMother(myParent);
				m=true;
				if(p==true)//both parents have already exist, so no need to add them in parent list
					return;
			}
			if(papa.equal(myParent))//they are the same person
			{
				child.setFather(myParent);
				p=true;
				if(m==true)
					return;
			}
		}
		
		if(m==false)
		{
			parent.add(mama);
			child.setMother(mama);
		}
			
		if(p==false)
		{
			parent.add(papa);
			child.setFather(papa);
		}
	}

	public void checkInheritance()
	{
		int [] upTree=new int[4];
		for(int i=0;i<4;i++)
		{
			upTree[i]=-1;
		}
		Node A1=this.A.getFather();
		Node A2=this.A.getMother();
		Node B1=this.B.getFather();
		Node B2=this.B.getMother();

		checkHelper(A1,A2,B1,B2,upTree);
		
		setLine(upTree);
		
		
	}
	private void checkHelper(Node A1, Node A2, Node B1, Node B2,int[] upTree)
	{
		if(A1==null || A2==null || B1== null || B2==null)
			return;
		
		int a1=-1,a2=-1,b1=-1,b2=-1;
		if(upTree[0]==-1)
			a1=(int)(Math.random()*2);
		if (upTree[1]==-1)
			a2=(int)(Math.random()*2);
		if(upTree[2]==-1)
			b1=(int)(Math.random()*2);
		if(upTree[3]==-1)
			b2=(int)(Math.random()*2);
		
		if(a1!=-1)
		{
			if(a1==a2&&A1.equals(A2)){
				upTree[1]=0;
			}
			if(a1==b1&&A1.equals(B1))
				upTree[2]=0;
			if(a1==b2&&A1.equals(B2))
				upTree[3]=0;
		}
		if( a2 != -1)
		{
			if( a2==b1 && A2.equals(B1))
					upTree[2]=1;
			if( a2 == b2 && A2.equals(B2))
					upTree[3]=1;
		}
		if( b1 != -1)
		{
			if( b1 == b2 && B1.equals(B2))
				upTree[3]=2;
		}
		//prepare for the next level
		//if X_e=0 then current allele inherits from its grand-paternal allele, otherwise, it inherits from its grand-maternal allele
		if(upTree[0]==-1){
			if(a1==0)
				A1=A1.getFather();
			else A1=A1.getMother();
		}
		
		
		if( upTree[1] == -1 ){
			if(a2==0)
				A2=A2.getFather();
			else A2=A2.getMother();
		}
		
		
		if( upTree[2] == -1){
			if(b1==0)
				B1=B1.getFather();
			else B1=B1.getMother();
		}
		
		
		if ( upTree[3] == -1){
			if(b2==0)
				B2=B2.getFather();
			else B2=B2.getMother();
		}
		
		checkHelper(A1,A2,B1,B2,upTree);
	}
	private void setLine(int [] upTree)
	{
		boolean [] lines=new boolean[6];
		int counter=0;
		for(int i=0;i<upTree.length;i++)
		{
			if(upTree[i]==-1)
				counter++;
		}
		if(counter ==1){
			for(int i=0;i<6;i++)
				lines[i]=true;
			setIBD(lines,upTree);
			return;
		}
		
		if (upTree[1]==0)
			lines[0]=true;
		if( upTree[2]==0)
		{
			lines[1]=true;
			if( upTree[1]==0)
				lines[5]=true;
		}
		if( upTree[2]==1)
		{
			lines[5]=true;
			if( upTree[1]==0)
			{
				lines[1]=true;
			}
		}
		if( upTree[3]==0 )
		{
			lines[4]=true;
			if(upTree[2]==0)
				lines[3]=true;
			if(upTree[1]==0)
				lines[2]=true;
		}
		if( upTree[3]==1)
		{
			lines[2]=true;
			if(upTree[2]==1)
				lines[3]=true;
			if(upTree[1]==0)
				lines[4]=true;
				
		}
		if(upTree[3]==2)
		{
			lines[3]=true;
			if( upTree[2]==1)
				lines[2]=true;
			if( upTree[2]==0)
				lines[4]=true;
		}
		setIBD(lines,upTree);
	}
	
	private void setIBD(boolean [] lines, int[] upTree)
	{
		
		if(!lines[0]&&!lines[1]&&!lines[2]&&!lines[3]&&!lines[4]&&!lines[5])
		{
			IBD[0]+=1;
			
		}
		else if(!lines[0]&&lines[1]&&!lines[2]&&!lines[3]&&!lines[4]&&!lines[5])        
		{
			IBD[1]+=1;
		}
		else if(!lines[0]&&!lines[1]&&lines[2]&&!lines[3]&&!lines[4]&&!lines[5])        
		{
			IBD[2]+=1;
		}
		else if(lines[0]&&!lines[1]&&!lines[2]&&!lines[3]&&!lines[4]&&!lines[5])
		{
        	IBD[3]+=1;
		}
		else if(!lines[0]&&!lines[1]&&!lines[2]&&lines[3]&&!lines[4]&&!lines[5])   
		{
			IBD[4]+=1;
		}
		else if(!lines[0]&&!lines[1]&&!lines[2]&&!lines[3]&&lines[4]&&!lines[5])
		{
			IBD[5]+=1;
		}
		else if(!lines[0]&&!lines[1]&&!lines[2]&&!lines[3]&&!lines[4]&&lines[5])
		{
			IBD[6]+=1;
		}
		else if(lines[0]&&!lines[1]&&lines[2]&&!lines[3]&&lines[4]&&!lines[5])
		{
			IBD[7]+=1;
		}
		else if(lines[0]&&lines[1]&&!lines[2]&&!lines[3]&&!lines[4]&&lines[5])
		{
			IBD[8]+=1;			
		}
		else if(!lines[0]&&lines[1]&&!lines[2]&&lines[3]&&lines[4]&&!lines[5])
		{
			IBD[9]+=1;
		}
		else if(!lines[0]&&!lines[1]&&lines[2]&&lines[3]&&!lines[4]&&lines[5])
		{
			IBD[10]+=1;
		}
		else if(lines[0]&&!lines[1]&&!lines[2]&&lines[3]&&!lines[4]&&!lines[5])
		{
			IBD[11]+=1;
		}
		else if(!lines[0]&&lines[1]&&lines[2]&&!lines[3]&&!lines[4]&&!lines[5])
		{
			IBD[12]+=1;

		}
		else if(!lines[0]&&!lines[1]&&!lines[2]&&!lines[3]&&lines[4]&&lines[5])
		{
			IBD[13]+=1;
		}
		else if(lines[0]&&lines[1]&&lines[2]&&lines[3]&&lines[4]&&lines[5])
		{
			IBD[14]+=1;
		
		}
			

	}
	
	
	public double getKinship(Node a, Node b, int N){

		return 1*getKinshipHelper(a,b, N);

	}



	public double getKinshipHelper(Node a, Node b, int N){
		// check founder:
		boolean both_founder = (a.getG()==0&&b.getG()==0);
		
		// base case:
		if(a.equal(b)&&both_founder){
			return 0.5;
		}
		if(!a.equal(b)&&both_founder){
			return 0;
		}
		
		int aid = a.getId(); int ag = a.getG();
		int bid = b.getId(); int bg = b.getG();
		
		// hashing the code in the table:
		int posa = ag*N+(aid);
		int posb = bg*N+(bid);
		
		if(kinship[posa][posb] != 0){
			return kinship[posa][posb];
		}		
		
		// recursive case:
		// check generation to force expand by level:
		
		if(a.equal(b)){
			kinship[posa][posb] =  (1 + getKinshipHelper(b.getMother(), b.getFather(), N))/2;
			kinship[posb][posa] = kinship[posa][posb];
			return kinship[posa][posb];
		}
		
		if(a.getG()>=b.getG()){ // expand a
			kinship[posa][posb] =  (getKinshipHelper(a.getMother(), b, N) + getKinshipHelper(a.getFather(), b, N))/2;
			kinship[posb][posa] = kinship[posa][posb];
			return kinship[posa][posb];
		}
		if(a.getG()<b.getG()){ // expand b
			kinship[posa][posb] =  (getKinshipHelper(a, b.getMother(), N) + getKinshipHelper(a, b.getFather(), N))/2;
			kinship[posb][posa] = kinship[posa][posb];
			return kinship[posa][posb];
		}
		return kinship[posa][posb];
	}
	public double [] getProb(int cPath){
		double[] IBDprob=new double [15];
		for(int i=0;i<15;i++)
			IBDprob[i]=(double)IBD[i]/(double)cPath;
		return IBDprob;
	}
	public void clearIBD(){
		for(int i=0;i<15;i++)
			IBD[i]=0;
	}
	/**
	 * used to debug
	 */
	public void printIBD()
	{
		for(int i=0;i<IBD.length;i++)
			System.out.print(IBD[i]+" ");
		System.out.println();
	}
	
}
