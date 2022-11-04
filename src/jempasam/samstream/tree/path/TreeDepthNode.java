package jempasam.samstream.tree.path;

public class TreeDepthNode<T,W> {
	
	
	
	public final T node;
	public final W weight;
	public final int depth;
	
	
	
	public TreeDepthNode(T node, W weight, int depth) {
		super();
		this.node = node;
		this.depth = depth;
		this.weight=weight;
	}
	
	
	
	@Override
	public String toString() {
		return depth+": "+weight+"->"+node;
	}
	
	public String toIndentedString(String str) {
		StringBuilder sb=new StringBuilder();
		for(int i=0; i<depth; i++)sb.append(str);
		sb.append(node).append(" (").append(weight).append(")");
		return sb.toString();
	}
	
	
	
}
