package jempasam.samstream.adapter;

import java.util.function.Consumer;
import java.util.function.Function;
import jempasam.samstream.stream.SamStream;

public class UniqueFunctionSStream<T> implements SamStream<T>, Consumer<Boolean>{
	
	
	
	private Function<Consumer<Boolean>,T> tryNext;
	private boolean succeed;
	
	
	
	public UniqueFunctionSStream(Function<Consumer<Boolean>,T> tryNext) {
		super();
		this.tryNext = tryNext;
		succeed=true;
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
	@Override
	public T tryNext() {
		return tryNext.apply(this);
	}
	
	@Override
	public void accept(Boolean t) {
		succeed=t;
	}

}
