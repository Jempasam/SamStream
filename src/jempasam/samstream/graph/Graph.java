package jempasam.samstream.graph;

import jempasam.samstream.stream.SamStream;

public interface Graph<P,W> {
	SamStream<GraphLink<P, W>> getLinks(P parent);
	
	public static class GraphLink<P,W>{
		public final P childNode;
		public final W linkWeight;
		public GraphLink(P childNode, W linkWeight) {
			super();
			this.childNode = childNode;
			this.linkWeight = linkWeight;
		}
	}
}
