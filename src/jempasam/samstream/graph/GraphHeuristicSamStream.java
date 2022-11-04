package jempasam.samstream.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jempasam.samstream.SamStreams;
import jempasam.samstream.graph.Graph.GraphLink;
import jempasam.samstream.graph.path.GraphNodePath;
import jempasam.samstream.stream.SamStream;

public class GraphHeuristicSamStream<N,L> implements SamStream<GraphNodePath<N, L>>{
	
	
	
	private Graph<N, L> graph;
	private Set<N> nodeFinded;
	private N start;
	private List<GraphNodePath<N, L>> actualNodes;
	private boolean succeed;
	
	private Comparator<GraphNodePath<N, L>> heuristic;
	
	
	
	public GraphHeuristicSamStream(Graph<N, L> graph, N start, Comparator<GraphNodePath<N, L>> heuristic) {
		super();
		this.graph = graph;
		this.heuristic = heuristic;
		this.start = start;
		
		nodeFinded=new HashSet<>();
		actualNodes=new ArrayList<>();
		reset();
	}
	
	
	
	@Override
	public void reset() {
		nodeFinded.clear();
		actualNodes.clear();
		nodeFinded.add(start);
		actualNodes.add(new GraphNodePath<>(start));
		succeed=true;
	}
	
	
	
	@Override
	public GraphNodePath<N, L> tryNext() {
		if(actualNodes.isEmpty()) {
			succeed=false;
			return null;
		}
		else {
			GraphNodePath<N, L> select=SamStreams.create(actualNodes).max(heuristic);
			actualNodes.remove(select);
			for(GraphLink<N, L> child : graph.getLinks(select.getLastValue())) {
				if(!nodeFinded.contains(child.childNode)) {
					nodeFinded.add(child.childNode);
					actualNodes.add(new GraphNodePath<>(select, child.linkWeight, child.childNode));
				}
			}
			return select;
		}
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
}
