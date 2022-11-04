package jempasam.samstream.tree;

public class TreeMove<N,W> {
	
	
	
	private TreeMoveDirection direction;
	private N node;
	private W weight;
	
	
	
	public TreeMove(TreeMoveDirection direction, N node, W weight) {
		super();
		this.direction = direction;
		this.node = node;
		this.weight = weight;
	}
	
	
	
	public TreeMoveDirection getDirection() {
		return direction;
	}
	
	public N getNode() {
		return node;
	}

	public W getWeight() {
		return weight;
	}
	
	
	@Override
	public String toString() {
		return direction+" to -["+weight+"]->"+node;
	}
}
