package jempasam.samstream.stream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jempasam.samstream.collectors.SamCollector;

public class ParallelSamStream<T> implements SamStream<T>{
	
	
	
	private SamStream<T> decorated;
	private ExecutorService executor;
	
	
	
	public ParallelSamStream(SamStream<T> decorated) {
		super();
		this.decorated = decorated;
		this.executor = Executors.newWorkStealingPool();
	}
	
	
	
	// Action
	@Override
	public void forEachRemaining(Consumer<? super T> action) {
		decorated.forEachRemaining(element->{
			executor.submit(()->action.accept(element));
		});
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) { }
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		decorated.reset();
		forEachRemaining(action);
	}
	
	@Override
	public void forEach(BiConsumer<SamStream<T>,T> action) {
		decorated.reset();
		forEachRemaining(action);
	}
	
	@Override
	public void forEachRemaining(BiConsumer<SamStream<T>,T> action) {
		decorated.forEachRemaining((stream,element)->{
			executor.submit(()->action.accept(stream,element));
		});
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) { }
	}
	
	@Override
	public <M,O> O collectRemaining(SamCollector<T, M, O> collector) {
		forEachRemaining(input -> {
			collector.give(input);
		});
		return collector.getResult();
	}
	
	@Override
	public <M,O> O collect(SamCollector<T, M, O> collector) {
		decorated.reset();
		return collectRemaining(collector);
	}
	
	

	@Override
	public T tryNext() {
		return decorated.tryNext();
	}

	@Override
	public boolean hasSucceed() {
		return decorated.hasSucceed();
	}
}
