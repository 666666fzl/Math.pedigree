package baseModel;

import java.util.ArrayList;
import java.util.List;


public class Individual {

	public String myName;
	public List<Integer> myParents;
	public String myEqual;
	
	public Individual(String name){
		this.myName = name;
		this.myParents = new ArrayList<Integer>();
		this.myEqual = "no";
	}
	
}
