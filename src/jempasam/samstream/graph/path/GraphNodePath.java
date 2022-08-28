package jempasam.samstream.graph.path;

import jempasam.samstream.SamStreams;
import jempasam.samstream.collectors.SamCollectors;
import jempasam.samstream.stream.AbstractSamStream;
import jempasam.samstream.stream.SamStream;

public class GraphNodePath<N,L> extends AbstractSamStream<GraphNodePath<N, L>>{
	
	
	
	private N value;
	private L linkWeight;
	private GraphNodePath<N,L> parent;
	private int depth;
	
	private GraphNodePath<N,L> actual;
	private boolean succeed;
	
	
	
	public GraphNodePath(GraphNodePath<N, L> parent, L weight, N value) {
		super();
		this.value = value;
		this.parent = parent;
		this.depth = parent.depth+1;
		this.linkWeight=weight;
		reset();
	}
	
	public GraphNodePath(N value) {
		this.value = value;
		this.parent = null;
		this.linkWeight=null;
		this.depth = 0;
		reset();
	}
	
	
	
	public N getLastValue() {
		return value;
	}

	public L getLastWeight() {
		return linkWeight;
	}

	public GraphNodePath<N, L> getLastParent() {
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
	public GraphNodePath<N, L> tryNext() {
		if(actual!=null) {
			GraphNodePath<N, L> ret=actual;
			actual=actual.parent;
			return ret;
		}
		else {
			succeed=false;
			return null;
		}
	}
	
	public SamStream<N> values(){
		return notLast().map(GraphNodePath::getLastValue);
	}
	
	public SamStream<L> weights(){
		return notLast().map(GraphNodePath::getLastWeight);
	}
	
	public SamStream<GraphNodePath<N, L>> notLast(){
		return filter(pathpart->pathpart.parent!=null);
	}
	
	@Override
	public String toString() {
		return map(part->part.value+(part.linkWeight!=null ? " <-"+part.linkWeight+"-" : "")) .collect(SamCollectors.concatenate(" "));
	}
	
}
