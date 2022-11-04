package jempasam.samstream.adapter;

import java.util.function.Consumer;

import jempasam.samstream.stream.SamStream;

public class SingletonSStream<T> implements SamStream<T>{
	
	
	
	private T value;
	private byte counter;
	
	
	
	public SingletonSStream(T value) {
		super();
		this.value=value;
		counter=0;
	}
	
	
	
	@Override
	public boolean hasSucceed() {
		return counter<2;
	}
	
	@Override
	public T tryNext() {
		counter++;
		if(counter<2)return value;
		else return null;
	}
	
	@Override
	public synchronized void syncNext(Consumer<T> action) {
		if(counter<1) {
			action.accept(value);
			counter++;
		}
	}
	
	@Override
	public synchronized void reset() {
		counter=0;
	}

}
