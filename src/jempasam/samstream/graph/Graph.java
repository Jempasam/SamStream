package jempasam.samstream.graph;

import java.util.Comparator;

import jempasam.samstream.graph.path.GraphNodePath;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.tree.GraphTreeSamStream;
import jempasam.samstream.tree.TreeSamStream;

public interface Graph<P,W> {
	
	
	
	SamStream<GraphLink<P, W>> getLinks(P parent);
	
	
	
	default TreeSamStream<P, W> treeStream(P start){
		return new GraphTreeSamStream<>(this,start);
	}
	
	default GraphSamStream<P, W> stream(P start){
		return new GraphSamStream<>(this,start);
	}
	
	default GraphHeuristicSamStream<P, W> stream(P start, Comparator<GraphNodePath<P,W>> heuristic){
		return new GraphHeuristicSamStream<>(this,start,heuristic);
	}
	
	
	
	public static class GraphLink<P,W>{
		public final P childNode;
		public final W linkWeight;
		public GraphLink(P childNode, W linkWeight) {
			super();
			this.childNode = childNode;
			this.linkWeight = linkWeight;
		}
		@Override
		public String toString() {
			return "-["+linkWeight+"]->"+childNode;
		}
	}
}
