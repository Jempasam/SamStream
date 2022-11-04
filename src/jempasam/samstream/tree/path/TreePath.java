package jempasam.samstream.tree.path;

import jempasam.samstream.collectors.SamCollectors;
import jempasam.samstream.stream.SamStream;

public class TreePath<N,W> implements SamStream<TreePath<N,W>>{
	
	
	
	private N value;
	private W linkWeight;
	private TreePath<N,W> parent;
	private int depth;
	
	private TreePath<N,W> actual;
	private boolean succeed;
	
	
	
	public TreePath(TreePath<N,W> parent, W weight, N value) {
		super();
		this.value = value;
		this.parent = parent;
		this.depth = parent.depth+1;
		this.linkWeight=weight;
		reset();
	}
	
	public TreePath(N value) {
		this.value = value;
		this.parent = null;
		this.depth = 0;
		reset();
	}
	
	
	
	public N getLastValue() {
		return value;
	}
	
	public W getLinkWeight() {
		return linkWeight;
	}

	public TreePath<N,W> getLastParent() {
		return parent;
	}

	public int getLastDepth() {
		return depth;
	}
	
	@Override
	public void reset() {
		actual=this;
		succeed=true;
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
	@Override
	public TreePath<N,W> tryNext() {
		if(actual!=null) {
			TreePath<N,W> ret=actual;
			actual=actual.parent;
			return ret;
		}
		else {
			succeed=false;
			return null;
		}
	}
	
	public SamStream<N> values(){
		return notLast().map(TreePath::getLastValue);
	}
	
	public SamStream<W> weights(){
		return notLast().map(TreePath::getLinkWeight);
	}
	
	public SamStream<TreePath<N,W>> notLast(){
		return filter(pathpart->pathpart.parent!=null);
	}
	
	@Override
	public String toString() {
		return map(part->part.value+" <--["+part.linkWeight+"]-" ) .collect(SamCollectors.concatenate(" "));
	}
	
}
