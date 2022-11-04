package jempasam.samstream.adapter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import jempasam.samstream.stream.SamStream;

public class GeneratorSStream<T> implements SamStream<T>{
	
	
	
	private Supplier<T> generator;
	
	
	
	public GeneratorSStream(Supplier<T> generator) {
		super();
		this.generator = generator;
	}
	
	@Override
	public boolean hasSucceed() {
		return true;
	}
	
	@Override
	public T tryNext() {
		return generator.get();
	}
	
	@Override
	public void syncNext(Consumer<T> action) {
		action.accept(generator.get());
	}
	
	@Override
	public void reset() {
	}

}
