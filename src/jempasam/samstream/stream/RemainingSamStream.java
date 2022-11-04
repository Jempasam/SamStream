package jempasam.samstream.stream;

import java.util.function.Consumer;

public class RemainingSamStream<T> implements SamStream<T>{
	
	
	
	private SamStream<T> decorated;
	
	
	
	public RemainingSamStream(SamStream<T> decorated) {
		super();
		this.decorated = decorated;
	}
	
	
	
	@Override
	public void reset() {
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
