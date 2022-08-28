package jempasam.samstream.graph;

import java.util.HashMap;
import jempasam.samstream.SamStreams;
import jempasam.samstream.stream.SamStream;

public class SimpleGraph<N,L> implements Graph<N, L>{
	
	
	
	private HashMap<N, GraphNode> map;
	
	
	
	public SimpleGraph() {
		map=new HashMap<>();
	}
	
	
	
	@Override
	public SamStream<GraphLink<N, L>> getLinks(N parent) {
		GraphNode node=map.get(parent);
		if(node!=null) return SamStreams.create(node.links.values());
		else return SamStreams.empty();
	}
	
	
	
	public void addLink(N from, N to, L weight) {
		GraphNode node=map.get(from);
		if(node==null) {
			node=new GraphNode();
			map.put(from, node);
		}
		node.links.put(to, new GraphLink<>(to, weight));
	}
	
	public void addBidirectionnalLink(N from, N to, L weight) {
		addLink(from, to, weight);
		addLink(to, from, weight);
	}
	
	public void removeLink(N from, N to) {
		GraphNode node=map.get(from);
		if(node==null) {
			node=new GraphNode();
			map.put(from, node);
		}
		node.links.remove(to);
	}
	
	private class GraphNode{
		
		public HashMap<N, Graph.GraphLink<N,L>> links;
		
		public GraphNode() {
			links=new HashMap<>();
		}
	}
}
