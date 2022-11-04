package jempasam.samstream.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import jempasam.samstream.SamStreams;
import jempasam.samstream.collectors.SamCollector;
import jempasam.samstream.collectors.SamCollectors;

/**
 * Une alternative à l'API Stream standard qui permet de créer facilement des nouveaux types de streams.
 * 
 * @author Samuel Demont
 *
 * @param <T>
 */
public interface SamStream<T> extends BaseSamStream<T>, Iterable<T>{
	
	/**
	 * Reset the stream to the first element.
	 * @exception UnsupportedOperationException This stream is not resettable.
	 */
	@Override
	default void reset() {
		throw new UnsupportedOperationException("Unresettable SamStream. Use \"remaining\" methods variant instead.");
	}
	
	/**
	 * Try to get the next element and then give it to the consumer if succeed.
	 * @param setter 
	 * @return Did succeed
	 */
	default boolean next(Consumer<T> setter) {
		T next=tryNext();
		if(hasSucceed()) {
			setter.accept(next);
			return true;
		}
		else return false;
	}
	
	default void syncNext(Consumer<T> action) {
		synchronized (this) {
			T v=tryNext();
			if(hasSucceed())action.accept(v);
		}
	}
	
	/**
	 * Try to get the next element and return an optional
	 * @return The next element or empty optional if the end of the stream is reached.
	 */
	default Optional<T> nextOptional(){
		T next=tryNext();
		if(hasSucceed()) {
			return Optional.of(next);
		}
		else return Optional.empty();
	}
	
	// Transform Stream
	/**
	 * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream.
	 * @param <O> new stream type
	 * @param mapper
	 * @return the new stream
	 */
	default <O> SamStream<O> map(Function<T, O> mapper){
		return new MapSStream<>(this,mapper);
	}
	
	default SamStream<T> intercepted(Consumer<T> intercepter){
		return new InterceptSStream<>(this,intercepter);
	}
	
	default <O> SamStream<O> filterCast(Class<O> type){
		return filter(i->type.isAssignableFrom(i.getClass())).map(type::cast);
	}
	
	default <O, S extends SamStream<O>> S turnInto(Function<SamStream<T>, S> factory){
		return factory.apply(this);
	}
	
	/**
	 * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream and their position in the stream.
	 * @param <O> new stream type
	 * @param mapper
	 * @return the new stream
	 */
	default <O> SamStream<O> map(BiFunction<Integer, T, O> mapper){
		return new Map2SStream<>(this,mapper);
	}
	
	/**
	 * Returns a stream consisting of the results of the Object::toString method the given
     * function to the elements of this stream.
	 * @return the new stream
	 */
	default SamStream<String> mapToString(){
		return new MapSStream<>(this,String::valueOf);
	}
	
	/**
	 * Returns a stream consisting of the results of applying the given
     * function to a pair of elements of this stream and another stream .
	 * @param <O> new stream type
	 * @param stream the second stream
	 * @param mapper
	 * @return the new stream
	 */
	default <O,U> SamStream<O> bimap(SamStream<U> stream, BiFunction<T,U,O> mapper){
		return new BiMapSStream<>(this, stream, mapper);
	}
	
	default <O> SamStream<O> reduced(Supplier<? extends O> factory, Predicate<? super T> doCreate, BiFunction<? super O,? super  T,? extends O> reducer){
		return new ReduceSStream<>(this, factory, doCreate, reducer);
	}
	
	default <O> SamStream<O> reduced(Supplier<O> factory, Predicate<T> doCreate, BiConsumer<O, T> reducer){
		return new ReduceSStream<>(this, factory, doCreate, (out,value)->{reducer.accept(out, value); return out;});
	}
	
	default SamStream<Numerated<T>> numerate(){
		return new Map2SStream<>(this,Numerated::new);
	}
	
	default <O> SamStream<O> flatMap(Function<T, SamStream<O>> mapper){
		return new FlattenSStream<>(new MapSStream<>(this, mapper));
	}
	
	default SamStream<T> then(Collection<SamStream<T>> collection){
		List<SamStream<T>> list=new ArrayList<>();
		list.add(this);
		list.addAll(collection);
		return new CombineSStream<>(list);
	}
	
	default SamStream<T> then(SamStream<T> stream){
		List<SamStream<T>> list=new ArrayList<>();
		list.add(this);
		list.add(stream);
		return new CombineSStream<>(list);
	}
	
	default SamStream<T> after(Collection<SamStream<T>> collection){
		List<SamStream<T>> list=new ArrayList<>();
		list.addAll(collection);
		list.add(this);
		return new CombineSStream<>(list);
	}
	
	default SamStream<T> after(SamStream<T> before){
		List<SamStream<T>> list=new ArrayList<>();
		list.add(before);
		list.add(this);
		return new CombineSStream<>(list);
	}
	
	@SuppressWarnings("unchecked")
	default SamStream<T> after(SamStream<T> ...collection){
		List<SamStream<T>> list=new ArrayList<>();
		Collections.addAll(list, collection);
		list.add(this);
		return new CombineSStream<>(list);
	}
	
	
	// Limit
	default SamStream<T> skip(int skipped){
		return new AtResetSStream<>(this, stream->{
			for(int i=0; i<skipped; i++)stream.tryNext();
		});
	}
	
	default SamStream<T> limit(int countlimit){
		return new CounterSStream<>(this, countlimit);
	}
	
	
	// Test
	/**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
     *
     * @param test predicate to apply to each element to determine if it should be included
     * @return the new stream
     */
	default SamStream<T> filter(Predicate<T> test){
		return new FilterSStream<>(this, test);
	}
	
	/**
     * Returns a stream consisting of the elements of this stream except null.
     *
     * @return the new stream
     */
	default SamStream<T> notNull(){
		return new FilterSStream<>(this, Objects::nonNull);
	}
	
	/**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
     *
     * @param test predicate to apply to each element and their position to determine if it should be included
     * @return the new stream
     */
	default SamStream<T> filter(BiPredicate<Integer,T> test){
		return new Filter2SStream<>(this, test);
	}
	
	default SamStream<T> until(Predicate<T> tester){
		return new UntilSStream<>(this,tester);
	}
	
	/**
     * Returns a stream consisting of the elements of distinct elements of this strea
     *
     * @return the new stream
     */
	default SamStream<T> distinct(){
		return new DistinctSStream<>(this);
	}
	
	
	// Action
	default void forEach(BiConsumer<SamStream<T>,T> action) {
		reset();
		T value;
		do {
			value=tryNext();
			if(hasSucceed())action.accept(this,value);
			else break;
		}while(true);
	}
	
	default void forEach(Consumer<? super T> action) {
		reset();
		T value;
		do {
			value=tryNext();
			if(hasSucceed())action.accept(value);
			else break;
		}while(true);
	}
	
	default void forEachUntilThrow(Consumer<? super T> action) throws Exception {
		reset();
		T value;
		do {
			value=tryNext();
			if(hasSucceed())action.accept(value);
			else break;
		}while(true);
	}
	
	default boolean ifOne(Predicate<T> test) {
		reset();
		T value=tryNext();
		while(hasSucceed()) {
			if(test.test(value))return true;
			value=tryNext();
		}
		return false;
	}
	
	default boolean ifAll(Predicate<T> test) {
		reset();
		T value=tryNext();
		while(hasSucceed()) {
			if(!test.test(value))return false;
			value=tryNext();
		}
		return true;
	}
	
	default T max(Comparator<T> comparator) {
		AtomicReference<T> ret=new AtomicReference<>(null);
		forEach(element->{
			if(ret.get()==null||comparator.compare(element, ret.get())>0) {
				ret.set(element);
			}
		});
		return ret.get();
	}
	
	default T min(Comparator<T> comparator) {
		AtomicReference<T> ret=new AtomicReference<>(null);
		forEach(element->{
			if(ret.get()==null||comparator.compare(element, ret.get())<0) {
				ret.set(element);
			}
		});
		return ret.get();
	}
	
	/**
     * Count the number of elements in the stream matching a predicate
     *
     *@param test the predicate
     * @return the new stream
     */
	default int count(Predicate<T> test) {
		reset();
		AtomicInteger count=new AtomicInteger(0);
		forEach(value->{
			if(test.test(value))count.incrementAndGet();
		});
		return count.get();
	}
	
	default <M,O> O collect(SamCollector<? super T, M, O> collector) {
		forEach(collector::give);
		return collector.getResult();
	}
	
	default <M,O> O collect(Collector<T, M, O> collector) {
		M container=collector.supplier().get();
		forEach( input -> collector.accumulator().accept(container, input) );
		return collector.finisher().apply(container);
	}
	
	default List<T> toList(){
		return collect(SamCollectors.toList());
	}
	
	default List<T> toList(T separator){
		return collect(SamCollectors.toList(separator));
	}
	
	/**
     * Collect all elements of the stream in a collection and create a stream of it.
     *
     * @return the new stream
     */
	default SamStream<T> fixed(){
		return SamStreams.create(toList());
	}
	
	default String toString(String separator){
		return mapToString().collect(SamCollectors.concatenate(separator));
	}
	
	default <O> O reduce(O from, BiFunction<O, T, O> action){
		reset();
		T value;
		do {
			value=tryNext();
			if(hasSucceed()) {
				from=action.apply(from, value);
			}
			else break;
		}while(true);
		return from;
	}
	
	default String asString() {
		StringBuilder sb=new StringBuilder();
		sb.append("[");
		forEach(val->sb.append(String.valueOf(val)).append(","));
		if(sb.length()>1)sb.setLength(sb.length()-1);
		sb.append("]");
		return sb.toString();
	}
	
	default Optional<T> next(){
		T value=tryNext();
		return hasSucceed() ? Optional.of(value) : Optional.empty();
	}
	
	default Optional<T> first(){
		reset();
		return next();
	}
	
	default Optional<T> last(){
		reset();
		T ret=tryNext();
		if(!hasSucceed()) {
			return Optional.empty();
		}
		T next=tryNext();
		while(hasSucceed()) {
			ret=next;
			next=tryNext();
		}
		return Optional.of(ret);
	}
	
	default BufferedSStream<T> buffered(int bufferSize){
		return new BufferedSStream<>(this, bufferSize);
	}
	
	default SamStream<T> parallel(){
		return new ParallelSamStream<>(this);
	}
	
	default SamStream<T> remaining(){
		return new RemainingSamStream<>(this);
	}
	
	default SStreamIterator<T> iterator(){
		reset();
		return new SStreamIterator<>(this);
	}
	
	
	
	abstract static class DecoratorSStream<I,O> implements SamStream<O>{	
		
		SamStream<I> input;
		
		
		DecoratorSStream(SamStream<I> input) {
			super();
			this.input = input;
		}
		
		public boolean hasSucceed() {
			return input.hasSucceed();
		}
		
		@Override
		public void reset() {
			input.reset();
		}
	}

	abstract static class SameDecoratorSStream<T> extends DecoratorSStream<T,T>{	
		
		SameDecoratorSStream(SamStream<T> input) {
			super(input);
			this.input = input;
		}
		
		@Override
		public boolean hasSucceed() {
			return input.hasSucceed();
		}
		
		@Override
		public void reset() {
			input.reset();
		}
		
		@Override
		public T tryNext() {
			return input.tryNext();
		}
	}

	static class MapSStream<I,O> extends DecoratorSStream<I,O>{	
		
		private Function<I, O> mapper;
		
		public MapSStream(SamStream<I> input, Function<I, O> mapper) {
			super(input);
			this.mapper=mapper;
		}
		
		@Override
		public O tryNext() {
			I ret=input.tryNext();
			return input.hasSucceed() ? mapper.apply(ret) : null;
		}
		
		@Override
		public synchronized void syncNext(Consumer<O> action) {
			input.syncNext(element->{
				action.accept(mapper.apply(element));
			});
		}
	}
	
	static class InterceptSStream<I> extends DecoratorSStream<I,I>{	
		
		private Consumer<I> intercepter;
		
		public InterceptSStream(SamStream<I> input, Consumer<I> intercepter) {
			super(input);
			this.intercepter=intercepter;
		}
		
		@Override
		public I tryNext() {
			I ret=input.tryNext();
			if(input.hasSucceed())intercepter.accept(ret);
			return ret;
		}
	}
	
	static class BiMapSStream<I,U,O> extends DecoratorSStream<I,O>{	
		
		private BiFunction<I,U, O> mapper;
		private SamStream<U> merged;
		
		public BiMapSStream(SamStream<I> input, SamStream<U> merged, BiFunction<I,U, O> mapper) {
			super(input);
			this.mapper=mapper;
			this.merged=merged;
		}
		
		@Override
		public O tryNext() {
			I ret=input.tryNext();
			U ret2=merged.tryNext();
			return input.hasSucceed()&&merged.hasSucceed() ? mapper.apply(ret,ret2) : null;
		}
		
		@Override
		public boolean hasSucceed() {
			return input.hasSucceed()&&merged.hasSucceed();
		}
		
		@Override
		public synchronized void syncNext(Consumer<O> action) {
			input.syncNext(element->{
				U element2=merged.tryNext();
				if(merged.hasSucceed())action.accept(mapper.apply(element,element2));
			});
		}
	}

	static class ReduceSStream<I,O> extends DecoratorSStream<I,O>{	
		
		private Supplier<? extends O> factory;
		private Predicate<? super I> doCreate;
		private BiFunction<? super O,? super  I,? extends O> reducer;
		private O reduced;
		
		public ReduceSStream(SamStream<I> input, Supplier<? extends O> factory, Predicate<? super I> doCreate, BiFunction<? super O,? super  I,? extends O> reducer) {
			super(input);
			this.factory=factory;
			this.doCreate=doCreate;
			this.reducer=reducer;
			reduced=factory.get();
		}
		
		@Override
		public O tryNext() {
			I value;
			do {
				value=input.tryNext();
				if(input.hasSucceed()) {
					if(doCreate.test(value)&&reduced!=null) {
						O ret=reduced;
						reduced=reducer.apply(factory.get(), value);
						return ret;
					}
					else{
						if(reduced==null)reduced=factory.get();
						reduced=reducer.apply(reduced, value);
					}
				}
				else break;
			}while(true);
			O ret=reduced;
			reduced=factory.get();
			return ret;
		}
		
		@Override
		public void reset() {
			super.reset();
			reduced=null;
		}
	}
	
	static class FlattenSStream<I> extends DecoratorSStream<SamStream<I>,I>{	
		
		private SamStream<I> stream;
		
		public FlattenSStream(SamStream<SamStream<I>> input) {
			super(input);
			this.stream=input.tryNext();
			if(this.input.hasSucceed())this.stream.reset();
			else this.stream=SamStreams.empty();
		}
		
		@Override
		public I tryNext() {
			if(this.stream==null) return null;
			I ret=stream.tryNext();
			while(!stream.hasSucceed()) {
				stream=input.tryNext();
				if(!input.hasSucceed())return null;
				stream.reset();
				ret=stream.tryNext();
			}
			return ret;
		}
		
		@Override
		public void reset() {
			super.reset();
			this.stream=input.tryNext();
			if(this.input.hasSucceed())this.stream.reset();
			else this.stream=SamStreams.empty();
		}
	}

	static class CombineSStream<I> implements SamStream<I>{	
		
		private List<SamStream<I>> parts;
		private int index;
		
		public CombineSStream(List<SamStream<I>> parts) {
			this.parts=parts;
			index=0;
		}
		
		@Override
		public I tryNext() {
			if(index>=parts.size())return null;
			SamStream<I> stream;
			I next;
			stream=parts.get(index);
			next=stream.tryNext();
			if(stream.hasSucceed())return next;
			else{
				do {
					index++;
					if(index>=parts.size())return null;
					stream=parts.get(index);
					next=stream.tryNext();
				}while(!stream.hasSucceed());
				return next;
			}
		}
		
		@Override
		public boolean hasSucceed() {
			return index<parts.size();
		}
		
		
		@Override
		public void reset() {
			index=0;
			parts.forEach(SamStream::reset);
		}
	}
	
	static class AtResetSStream<T> extends SameDecoratorSStream<T>{	
		
		private Consumer<SamStream<T>> mapper;
		
		public AtResetSStream(SamStream<T> input, Consumer<SamStream<T>> mapper) {
			super(input);
			this.mapper=mapper;
		}
		
		@Override
		public void reset() {
			super.reset();
			mapper.accept(this);
		}
	}
	
	static class Map2SStream<I,O> extends DecoratorSStream<I,O>{	
		
		private BiFunction<Integer,I, O> mapper;
		private int counter;
		
		public Map2SStream(SamStream<I> input, BiFunction<Integer,I, O> mapper) {
			super(input);
			this.mapper=mapper;
			this.counter=-1;
		}
		
		@Override
		public O tryNext() {
			I ret=input.tryNext();
			counter++;
			return input.hasSucceed() ? mapper.apply(counter,ret) : null;
		}
		
		@Override
		public synchronized void syncNext(Consumer<O> action) {
			int c;
			synchronized (this) {
				counter++;
				c=counter;
			}
			input.syncNext(element->{
				action.accept(mapper.apply(c, element));
			});
		}
		
		@Override
		public void reset() {
			super.reset();
			counter=-1;
		}
	}
	
	static class FilterSStream<I> extends DecoratorSStream<I,I>{	
		
		private Predicate<I> tester;
		
		public FilterSStream(SamStream<I> input, Predicate<I> tester) {
			super(input);
			this.tester=tester;
		}
		
		@Override
		public I tryNext() {
			I ret;
			boolean succeed;
			do {
				ret=input.tryNext();
				succeed=input.hasSucceed();
			} while(succeed && !tester.test(ret));
			if(succeed)return ret;
			else return null;
		}
		
		@Override
		public synchronized void syncNext(Consumer<I> action) {
			super.syncNext(element->{
				if(tester.test(element))action.accept(element);
			});
		}
	}

	static class Filter2SStream<I> extends DecoratorSStream<I,I>{	
		
		private BiPredicate<Integer,I> tester;
		private int counter;
		
		public Filter2SStream(SamStream<I> input, BiPredicate<Integer,I> tester) {
			super(input);
			this.tester=tester;
			this.counter=-1;
		}
		
		@Override
		public I tryNext() {
			I ret;
			boolean succeed;
			do {
				ret=input.tryNext();
				counter++;
				succeed=input.hasSucceed();
			} while(succeed && !tester.test(counter,ret));
			if(succeed)return ret;
			else return null;
		}
		
		@Override
		public synchronized void syncNext(Consumer<I> action) {
			int c;
			synchronized (this) {
				counter++;
				c=counter;
			}
			super.syncNext(element->{
				if(tester.test(c,element))action.accept(element);
			});
		}
		
		@Override
		public void reset() {
			super.reset();
			counter=-1;
		}
	}
	
	static class CounterSStream<I> extends DecoratorSStream<I,I>{	
		
		private int max;
		private int counter;
		
		public CounterSStream(SamStream<I> input, int max) {
			super(input);
			this.max=max;
			this.counter=0;
		}
		
		@Override
		public I tryNext() {
			counter++;
			return hasSucceed() ? input.tryNext() : null;
		}
		
		@Override
		public boolean hasSucceed() {
			return counter<=max && input.hasSucceed();
		}
		
		@Override
		public synchronized void syncNext(Consumer<I> action) {
			boolean doget=false;
			synchronized (this) {
				counter++;
				if(counter<=max)doget=true;
			}
			if(doget)input.syncNext(action);
		}
		
		@Override
		public void reset() {
			super.reset();
			counter=0;
		}
	}
	
	static class DistinctSStream<I> extends DecoratorSStream<I,I>{	
		
		private Set<I> set;
		
		public DistinctSStream(SamStream<I> input) {
			super(input);
			this.set=new HashSet<>();
		}
		
		private boolean testAndAdd(I val) {
			if(set.contains(val))return true;
			else {
				set.add(val);
				return false;
			}
		}
		
		@Override
		public I tryNext() {
			I ret;
			boolean succeed;
			do {
				ret=input.tryNext();
				succeed=input.hasSucceed();
			} while(succeed && testAndAdd(ret));
			
			if(succeed) return ret;
			else return null;
		}
		
		@Override
		public synchronized void syncNext(Consumer<I> action) {
			input.syncNext(element->{
				boolean toget=false;
				synchronized (this) {
					toget=!testAndAdd(element);
				}
				if(toget)action.accept(element);
			});
		}
		
		@Override
		public void reset() {
			super.reset();
			set.clear();
		}
	}
	
static class UntilSStream<I> extends DecoratorSStream<I,I>{
		
		private Predicate<I> tester;
		private boolean end;
		
		public UntilSStream(SamStream<I> input, Predicate<I> tester) {
			super(input);
			this.tester=tester;
			this.end=false;
		}
		
		@Override
		public I tryNext() {
			if(this.end)return null;
			I ret;
			ret=input.tryNext();
			if(!hasSucceed()) return null;
			if(tester.test(ret)) {
				this.end=true;
				return null;
			}
			return ret;
		}
		
		@Override
		public boolean hasSucceed() {
		return end && input.hasSucceed();
		}
		
		@Override
		public void reset() {
			super.reset();
			this.end=false;
		}
	}
	
	static class BufferedSStream<I> extends SameDecoratorSStream<I>{
		
		private List<I> buffer;
		private int end;
		private int actual;
		private int mark;
		
		@SuppressWarnings("unchecked")
		public BufferedSStream(SamStream<I> input, int buffersize) {
			super(input);
			this.buffer=Arrays.asList((I[])new Object[buffersize]);
			this.end=0;
			this.actual=0;
			this.mark=-1;
		}
		
		@Override
		public I tryNext() {
			if(actual!=end) {
				actual++;
				if(actual>=buffer.size())actual=0;
				return buffer.get(actual);
			}
			else if(walk()) {
				actual=end;
				return buffer.get(actual);
			}
			else return null;
		}
		
		private boolean walk() {
			I next=input.tryNext();
			if(input.hasSucceed()) {
				end++;
				if(end>=buffer.size())end=0;
				buffer.set(end, next);
				return true;
			}
			else return false;
		}
		
		public void back() {
			actual--;
			if(actual<0)actual=buffer.size()-1;
		}
		
		public void mark() {
			mark=actual;
		}
		
		public Marker marker() {
			return new Marker(actual, this);
		}
		
		public void goToMarker(Marker marker) {
			if(this==marker.stream)actual=marker.pos;
		}
		
		public void goToMark() {
			if(mark!=-1)actual=mark;
		}
		
		@Override
		public boolean hasSucceed() {
			return input.hasSucceed() || actual!=end;
		}
		
		public boolean hasNext() {
			if(actual!=end)return true;
			else {
				return walk();
			}
		}
		
		public I actual() {
			return buffer.get(actual);
		}
		
		public I get(int offset) {
			int size=buffer.size();
			return buffer.get((actual+offset+size)%size);
		}
		
		public I peek() {
			return get(1);
		}
		
		@Override
		public void reset() {
			super.reset();
			this.end=0;
			this.actual=0;
			this.mark=-1;
		}
		
		public static class Marker {
			int pos;
			BufferedSStream<?> stream;
			Marker(int pos, BufferedSStream<?> stream) {
				super();
				this.pos = pos;
				this.stream = stream;
			}
		}
		
	}
	
	public static class Numerated<T> {
		
		
		private T value;
		private int number;
		
		
		public Numerated(int number, T value) {
			super();
			this.number = number;
			this.value = value;
		}
		
		
		public T getValue() {
			return value;
		}
		
		public int getNumber() {
			return number;
		}
		
		@Override
		public String toString() {
			return number+": "+value;
		}
		
		
	}
	
	public static class SStreamIterator<T> implements Iterator<T>{
		
		private SamStream<T> input;
		private T next;
		private T actual;

		public SStreamIterator(SamStream<T> input) {
			super();
			this.input = input;
			next=input.tryNext();
		}
		
		@Override
		public T next() {
			if(!input.hasSucceed())throw new NoSuchElementException();
			actual=next;
			next=input.tryNext();
			return actual;
		}
		
		public T actual() {
			return actual;
		}
		
		public T peek() {
			return next;
		}
		
		@Override
		public boolean hasNext() {
			return this.input.hasSucceed();
		}
		
		
	}
}
