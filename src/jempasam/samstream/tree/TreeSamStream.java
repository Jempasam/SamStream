package jempasam.samstream.tree;

import java.util.ArrayList;
import java.util.List;
import jempasam.samstream.collectors.SamCollector;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.tree.path.TreeConnection;
import jempasam.samstream.tree.path.TreeDepthNode;
import jempasam.samstream.tree.path.TreePath;

public interface TreeSamStream<N,W> extends SamStream<TreeMove<N,W>> {
	
	
	
	default PathTSStream<N,W> paths(){
		return new PathTSStream<>(this);
	}
	
	default ConnectionTSStream<N,W> connections(){
		return new ConnectionTSStream<>(this);
	}
	
	default DepthTSStream<N,W> depthed(){
		return new DepthTSStream<>(this);
	}
	
	default SamStream<N> values(){
		return new NodeTSStream<>(this);
	}
	
	default SamStream<W> weights(){
		return new WeightTSStream<>(this);
	}
	
	default N treeReduce(N parent,MoveReducer<N,W> reducer) {
		return collect(treeReduceCollector(parent, reducer));
	}
	
	default String asTreeString() {
		return depthed().mapToIndentedString(" |").toString("\n");
	}
	
	public abstract static class DecoratorTSStream<N,W, T> implements SamStream<T>{
		
		protected TreeSamStream<N,W> decorated;
		
		protected DecoratorTSStream(TreeSamStream<N,W> decorated) {
			this.decorated = decorated;
		}
		
		@Override
		public boolean hasSucceed() {
			return decorated.hasSucceed();
		}
		
	}
	
	public static class PathTSStream<N,W> extends DecoratorTSStream<N, W, TreePath<N,W>>{
		
		private TreePath<N,W> path=null;

		public PathTSStream(TreeSamStream<N,W> decorated) {
			super(decorated);
		}
		
		@Override
		public TreePath<N,W> tryNext() {
			while(true) {
				TreeMove<N,W> move=decorated.tryNext();
				if(!decorated.hasSucceed())break;
				
				switch(move.getDirection()) {
					case CHILD:
						path=new TreePath<>(path, move.getWeight(), move.getNode());
						return path;
					case SIBLING:
						path=new TreePath<>(path.getLastParent(), move.getWeight(), move.getNode());
						return path;
					case PARENT:
						path=path.getLastParent();
						continue;
					case INITIAL:
						path=new TreePath<>(move.getNode());
						return path;
					default:
						return null;
				}
			}
			return null;
		}
		
		@Override
		public void reset() {
			decorated.reset();
			path=null;
		}
		
	}
	
	public static class DepthTSStream<N,W> extends DecoratorTSStream<N, W, TreeDepthNode<N,W>>{
		
		private int depth;
		
		public DepthTSStream(TreeSamStream<N,W> decorated) {
			super(decorated);
			depth=0;
		}
		
		@Override
		public TreeDepthNode<N,W> tryNext() {
			while(true) {
				TreeMove<N,W> move=decorated.tryNext();
				if(!decorated.hasSucceed())break;
				switch(move.getDirection()) {
					case CHILD:
						depth++;
					case SIBLING:
					case INITIAL:
						return new TreeDepthNode<>(move.getNode(), move.getWeight(), depth);
					case PARENT:
						depth--;
						continue;
					default:
						return null;
				}
			}
			return null;
		}
		
		@Override
		public void reset() {
			decorated.reset();
			depth=0;
		}
		
		public SamStream<String> mapToIndentedString(String indent){
			return map(de->de.toIndentedString(indent));
		}
		
	}
	
	public static class ConnectionTSStream<N,W> extends DecoratorTSStream<N,W, TreeConnection<N,W>>{
		
		private List<N> stack;

		public ConnectionTSStream(TreeSamStream<N, W> decorated) {
			super(decorated);
			stack=new ArrayList<>();
			stack.add(null);
		}
		
		@Override
		public TreeConnection<N, W> tryNext() {
			TreeMove<N, W> move=decorated.tryNext();
			if(decorated.hasSucceed()) {
				switch(move.getDirection()) {
					case CHILD:
						stack.add(move.getNode());
						return new TreeConnection<>(stack.get(stack.size()-2), move.getWeight(), move.getNode());
					case PARENT:
						stack.remove(stack.size()-1);
						return tryNext();
					case INITIAL:
					case SIBLING:
						stack.set(stack.size()-1, move.getNode());
						return new TreeConnection<>(stack.get(stack.size()-2), move.getWeight(), stack.get(stack.size()-1));
					default:
						return null;
				}
			}
			else return null;
		}
		
		@Override
		public void reset() {
			decorated.reset();
			stack.clear();
			stack.add(null);
		}
		
	}
	
	public static class NodeTSStream<N,W> extends DecoratorTSStream<N, W, N>{
		
		public NodeTSStream(TreeSamStream<N, W> decorated) {
			super(decorated);
		}
		
		@Override
		public N tryNext() {
			while(true) {
				TreeMove<N, W> move=decorated.tryNext();
				if(decorated.hasSucceed()) {
					switch(move.getDirection()) {
						case CHILD:
						case INITIAL:
						case SIBLING:
							return move.getNode();
						default:
							continue;
					}
				}
				else return null;
			}
		}
		
	}
	
	public static class WeightTSStream<N,W> extends DecoratorTSStream<N, W, W>{
		
		public WeightTSStream(TreeSamStream<N, W> decorated) {
			super(decorated);
		}
		
		@Override
		public W tryNext() {
			while(true) {
				TreeMove<N, W> move=decorated.tryNext();
				if(decorated.hasSucceed()) {
					switch(move.getDirection()) {
						case CHILD:
						case INITIAL:
						case SIBLING:
							if(move.getWeight()!=null) return move.getWeight();
						default:
							continue;
					}
				}
				else return null;
			}
		}
		
	}
	
	public static <N,W> SamCollector<TreeMove<N, W>, N, N> treeReduceCollector(N parent,MoveReducer<N,W> reducer){
		return new SamCollector<TreeMove<N,W>, N, N>() {
			int count=0;
			List<N> stack=new ArrayList<>();
			{ stack.add(parent); }
			
			@Override public int countIngredient() { return count; }
			
			@Override public N getResult() { return parent; }
			
			@Override public N getState() { return parent; }
			
			@Override
			public void give(TreeMove<N, W> ingredient) {
				count++;
				switch(ingredient.getDirection()) {
					case INITIAL:
					case CHILD:{
						N parent=stack.get(stack.size()-1);
						reducer.addChildTo(ingredient.getNode(), ingredient.getWeight(), parent);
						stack.add(parent);
						return;
					}
					case SIBLING:{
						N parent=stack.get(stack.size()-2);
						reducer.addChildTo(ingredient.getNode(), ingredient.getWeight(), parent);
						stack.set(stack.size()-1, ingredient.getNode());
						return;
					}
					case PARENT:{
						stack.remove(stack.size()-1);
						return;
					}
				}
			}
		};
	}
	
	static interface MoveReducer<N,W>{
		void addChildTo(N child, W linkWeight, N parent);
	}
	
}
