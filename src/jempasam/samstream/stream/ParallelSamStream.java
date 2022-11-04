package jempasam.samstream.stream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import jempasam.samstream.collectors.SamCollector;

public class ParallelSamStream<T> implements SamStream<T>{
	
	
	
	private SamStream<T> decorated;
	private ExecutorService executor;
	
	
	
	public ParallelSamStream(SamStream<T> decorated) {
		super();
		this.decorated = decorated;
		this.executor = Executors.newWorkStealingPool();
	}
	
	
	
	@Override
	public void forEach(Consumer<? super T> action) {
		List<Future<?>> towait=new LinkedList<>();
		decorated.forEach(element->{
			towait.add(executor.submit(()->action.accept(element)));
		});
		for(Future<?> w : towait) try { w.get(); }catch (Exception e) { }
	}
	
	@Override
	public void forEach(BiConsumer<SamStream<T>,T> action) {
		List<Future<?>> towait=new ArrayList<>();
		decorated.forEach((stream,element)->{
			towait.add(executor.submit(()->action.accept(stream,element)));
		});
		for(Future<?> w : towait) try { w.get(); }catch (Exception e) { }
	}
	
	@Override
	public <M,O> O collect(SamCollector<? super T, M, O> collector) {
		forEach(input -> {
			collector.give(input);
		});
		return collector.getResult();
	}
	
	public <O> O reduce(O from, BiFunction<O, T, O> action){
		AtomicReference<O> result=new AtomicReference<>(from);
		forEach(e-> result.updateAndGet(old->action.apply(old, e)));
		return result.get();
	}
	
	public boolean ifOne(Predicate<T> test) {
		AtomicBoolean ret=new AtomicBoolean(false);
		forEach(element->{
			if(test.test(element)) {
				ret.set(true);
				executor.shutdown();
			}
		});
		return ret.get();
	}
	
	public boolean ifAll(Predicate<T> test) {
		AtomicBoolean ret=new AtomicBoolean(true);
		forEach(element->{
			if(!test.test(element)) {
				ret.set(false);
				executor.shutdown();
			}
		});
		return ret.get();
	}
	
	@Override
	public T tryNext() {
		return decorated.tryNext();
	}

	@Override
	public boolean hasSucceed() {
		return decorated.hasSucceed();
	}
	
	@Override
	public void syncNext(Consumer<T> action) {
		decorated.syncNext(action);
	}
}
