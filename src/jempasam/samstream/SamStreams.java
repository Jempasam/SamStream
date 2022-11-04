package jempasam.samstream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jempasam.samstream.adapter.ArraySStream;
import jempasam.samstream.adapter.DoubleSupplierSStream;
import jempasam.samstream.adapter.GeneratorSStream;
import jempasam.samstream.adapter.IndexSStream;
import jempasam.samstream.adapter.IterableSStream;
import jempasam.samstream.adapter.IteratorSStream;
import jempasam.samstream.adapter.ListSStream;
import jempasam.samstream.adapter.RandomArraySStream;
import jempasam.samstream.adapter.RandomListSStream;
import jempasam.samstream.adapter.SingletonSStream;
import jempasam.samstream.adapter.UniqueFunctionSStream;
import jempasam.samstream.adapter.UniqueSupplierSStream;
import jempasam.samstream.graph.Graph;
import jempasam.samstream.graph.GraphHeuristicSamStream;
import jempasam.samstream.graph.GraphSamStream;
import jempasam.samstream.graph.path.GraphNodePath;
import jempasam.samstream.stream.SamStream;
import jempasam.samstream.stream.SamStream.Numerated;

public class SamStreams {
	private SamStreams() {}
	
	public static <T> SamStream<T> singleton(T value) {
		return new SingletonSStream<>(value);
	}
	
	public static <T> SamStream<T> create(Iterable<T> iterable) {
		return new IterableSStream<>(iterable);
	}
	
	
	@SafeVarargs
	public static <T> SamStream<T> create(T ...values) {
		return createAt(0,values);
	}
	
	@SafeVarargs
	public static <T> SamStream<T> createAt(int start, T ...values) {
		return new ArraySStream<>(values,start);
	}
	
	
	public static <T> SamStream<T> create(List<T> values) {
		return createAt(0,values);
	}
	
	public static <T> SamStream<T> createAt(int start, List<T> values) {
		return new ListSStream<>(values,start);
	}
	
	
	public static <T> SamStream<T> create(T value) {
		return new SingletonSStream<>(value);
	}
	
	public static <T> SamStream<T> create(Iterator<T> iterator) {
		return new IteratorSStream<>(iterator);
	}
	
	public static <T> SamStream<T> create(Stream<T> stream) {
		return new IteratorSStream<>(stream.iterator());
	}

	public static <T> SamStream<T> create(Supplier<T> tryNext) {
		return new UniqueSupplierSStream<>(tryNext);
	}
	
	public static <T> SamStream<T> create(IntPredicate hasSucceed, IntFunction<T> get) {
		return new IndexSStream<>(hasSucceed,get);
	}
	
	public static <T> SamStream<T> create(Function<Consumer<Boolean>,T> tryNext) {
		return new UniqueFunctionSStream<>(tryNext);
	}
	
	public static <T> SamStream<T> create(Supplier<T> tryNext, BooleanSupplier hasNext) {
		return new DoubleSupplierSStream<>(tryNext, hasNext);
	}
	
	
	
	public static <N,L> GraphSamStream<N, L> create(Graph<N, L> graph, N parent){
		return new GraphSamStream<N, L>(graph, parent);
	}
	
	public static <N,L> GraphHeuristicSamStream<N, L> create(Graph<N, L> graph, N parent, Comparator<GraphNodePath<N, L>> heuristic){
		return new GraphHeuristicSamStream<N, L>(graph, parent, heuristic);
	}
	
	
	
	public static <T> SamStream<T> generator(Supplier<T> generator) {
		return new GeneratorSStream<>(generator);
	}
	
	
	
	@SafeVarargs
	public static <T> SamStream<T> random(T ...values) {
		return new RandomArraySStream<>(values);
	}
	
	public static <T> SamStream<T> random(List<T> values) {
		return new RandomListSStream<>(values);
	}
	
	
	
	public static <T> SamStream<T> empty(){
		return new SamStream<T>() {
			public boolean hasSucceed() {return false;}
			public void syncNext(Consumer<T> action) { }
			public T tryNext() {return null;}
		};
	}
	
	public static final Predicate<Numerated<?>> EVEN=n->n.getNumber()%2==0;
	public static final Predicate<Numerated<?>> ODD=n->n.getNumber()%2==1;
}
