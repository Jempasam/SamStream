package jempasam.samstream.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jempasam.samstream.graph.Graph.GraphLink;
import jempasam.samstream.graph.path.GraphNodePath;
import jempasam.samstream.stream.SamStream;

public class GraphSamStream<N,L> implements SamStream<GraphNodePath<N, L>>{
	
	
	
	private Graph<N, L> graph;
	private Set<N> nodeFinded;
	private N start;
	
	// Actual nodes
	private List<GraphNodePath<N, L>> actualNodes;
	private List<GraphNodePath<N, L>> newnodes;
	
	// Now Node
	private Iterator<GraphNodePath<N, L>> actualNodesIt;
	private GraphNodePath<N, L> actualNode;
	
	// Now Child
	private Iterator<Graph.GraphLink<N,L>> actualChildsIt;
	
	private boolean succeed;
	
	
	
	public GraphSamStream(Graph<N, L> graph, N start) {
		super();
		this.graph = graph;
		this.start=start;
		
		nodeFinded=new HashSet<>();
		actualNodes=new ArrayList<>();
		newnodes=new ArrayList<>();
		reset();
	}
	
	
	
	@Override
	public void reset() {
		nodeFinded.clear();
		actualNodes.clear();
		newnodes.clear();
		nodeFinded.add(start);
		actualNodes.add(new GraphNodePath<>(start));
		actualNodesIt=actualNodes.iterator();
		if(actualNodesIt.hasNext()) {
			actualNode=actualNodesIt.next();
			actualChildsIt=graph.getLinks(actualNode.getLastValue()).iterator();
			succeed=true;
		}
		else succeed=false;
	}
	
	
	
	@Override
	public GraphNodePath<N, L> tryNext() {
		if(!succeed)return null;
		while(true) {
			if(actualChildsIt.hasNext()) {
				GraphLink<N,L> v=actualChildsIt.next();
				if(!nodeFinded.contains(v.childNode)) {
					nodeFinded.add(v.childNode);
					GraphNodePath<N, L> nnode=new GraphNodePath<>(actualNode, v.linkWeight, v.childNode);
					newnodes.add(nnode);
					
					return nnode;
				}
			}
			else {
				if(actualNodesIt.hasNext()) {
					actualNode=actualNodesIt.next();
					actualChildsIt=graph.getLinks(actualNode.getLastValue()).iterator();
				}
				else {
					if(newnodes.size()>0) {
						actualNodes=newnodes;
						newnodes=new ArrayList<>();
						actualNodesIt=actualNodes.iterator();
					}
					else break;
				}
			}
		}
		succeed=false;
		return null;
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
}
