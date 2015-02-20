package baseModel;
public class Node {
	private Node mother;
	private Node father;
	private int id;
	private int g;
	public Node(int id, int g) {
		this.id = id;
		this.g = g;
		this.mother = null;
		this.father = null;
	}

	// setter and getter
	public int getId() {
		return this.id;
	}

	public int getG() {
		return this.g;
	}
	public void setParent(Node mother, Node father) {
		if (this.mother == null)
			this.mother = mother;
		if (this.father == null)
			this.father = father;
	}
	
	public void setMother(Node mother) {
		this.mother = mother;

	}

	public void setFather(Node father) {
		this.father = father;
	}

	public Node getMother() {
		return this.mother;
	}

	public Node getFather() {
		return this.father;
	}

	public boolean equal(Node other) {
		if (other.id == this.id && other.g == this.g)
			return true;
		else
			return false;
	}

	public int getMotherId() {
		return this.mother.getId();
	}

	public int getFatherId() {
		return this.father.getId();
	}

}