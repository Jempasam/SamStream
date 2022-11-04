package jempasam.samstream.adapter;

import java.util.Random;
import java.util.function.Consumer;

import jempasam.samstream.stream.SamStream;

public class RandomArraySStream<T> implements SamStream<T>{
	
	
	
	private T[] array;
	private Random random;
	
	
	
	public RandomArraySStream(T[] array) {
		super();
		this.array=array;
		this.random=new Random();
	}
	
	@Override
	public boolean hasSucceed() {
		return true;
	}
	
	@Override
	public T tryNext() {
		return this.array[this.random.nextInt(this.array.length)];
	}
	
	@Override
	public synchronized void syncNext(Consumer<T> action) {
		action.accept(this.array[this.random.nextInt(this.array.length)]);
	}
	
	@Override
	public void reset() {
	}

}
