package jempasam.samstream.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jempasam.samstream.graph.Graph;
import jempasam.samstream.graph.Graph.GraphLink;
import jempasam.samstream.stream.SamStream;

public class GraphTreeSamStream<N,W> implements TreeSamStream<N, W>{
	
	
	
	public Graph<N, W> graph;
	public N first;
	public boolean isFirst;
	public boolean firstChild;
	
	private List<SamStream<GraphLink<N,W>>> stack;
	private Set<N> finded;
	private List<N> stackNode;
	
	
	
	public GraphTreeSamStream(Graph<N, W> graph, N first) {
		super();
		this.graph = graph;
		this.first = first;
		this.stack=new ArrayList<>();
		this.stackNode=new ArrayList<>();
		this.finded=new HashSet<>();
	}
	
	
	
	@Override
	public TreeMove<N, W> tryNext() {
		while(stack.size()>0) {
			if(isFirst) {
				isFirst=false;
				return new TreeMove<>(TreeMoveDirection.INITIAL, first, null);
			}
			else {
				SamStream<GraphLink<N,W>> last=stack.get(stack.size()-1);
				GraphLink<N, W> next=last.tryNext();
				if(last.hasSucceed()) {
					if(!finded.contains(next.childNode)) {
						finded.add(next.childNode);
						stackNode.add(next.childNode);
						stack.add(graph.getLinks(next.childNode));
						TreeMoveDirection direction=firstChild ? TreeMoveDirection.CHILD : TreeMoveDirection.SIBLING;
						firstChild=true;
						return new TreeMove<>(direction, next.childNode, next.linkWeight);
					}
				}
				else {
					int stacksize=stackNode.size();
					N parent=stacksize>1 ? stackNode.get(stackNode.size()-2) : null;
					stack.remove(stack.size()-1);
					stackNode.remove(stackNode.size()-1);
					if(!firstChild)return new TreeMove<>(TreeMoveDirection.PARENT, parent, null);
					else firstChild=false;
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean hasSucceed() {
		return stack.size()>0;
	}
	
	@Override
	public void reset() {
		stack.clear();
		stack.add(this.graph.getLinks(first));
		stackNode.add(first);
		finded.add(first);
		isFirst=true;
		firstChild=true;
	}
	
}
