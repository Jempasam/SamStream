package jempasam.samstream.tree.path;

public class TreeConnection<N, W> {
	
	
	
	private N parent;
	private W weight;
	private N child;
	
	
	
	public TreeConnection(N parent, W weight, N child) {
		super();
		this.parent = parent;
		this.weight = weight;
		this.child = child;
	}



	public N getParent() {
		return parent;
	}
	
	public W getWeight() {
		return weight;
	}
	
	public N getChild() {
		return child;
	}
	
	
	
}
