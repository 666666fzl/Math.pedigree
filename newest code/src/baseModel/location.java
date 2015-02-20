package baseModel;

public class location {
	private int g;// gth generation
	private int Mi;// mother indices
	private int Fi;// father indices
	public int group;
	
	/**
	 * location constructor
	 * @param other
	 */
	public location(location other){
		this.g=other.g;
		this.Mi=other.Mi;
		this.Fi=other.Fi;
	}
	public location(int g, int Mi, int Fi){
		this.g=g;
		this.Fi=Fi;
		this.Mi=Mi;
		this.group=0;
	}
	public void setParent(int Mi, int Fi)
	{
		this.Fi=Fi;
		this.Mi=Mi;
		
	}
	public String printLocation()
	{
		String result="G "+g+" "+" M "+Mi+" F "+Fi;
		return result;
	}
	public int getG(){
		return this.g;
	}
	public int getFi(){
		return this.Fi;
	}
	public int getMi(){
		return this.Mi;
	}
	public int getGroup(){
		return this.group;
	}
	public void setGroup(int group){
		this.group=group;
	}
	
	public boolean identical(location other){
		if(this.g==other.g && this.Mi==other.Mi && this.Fi==other.Fi) return true;
		else return false;
	}
}
