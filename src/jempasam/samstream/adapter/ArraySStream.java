package jempasam.samstream.adapter;

import java.util.function.Consumer;

import jempasam.samstream.stream.AbstractSamStream;
import jempasam.samstream.stream.SamStream;

public class ArraySStream<T> extends AbstractSamStream<T> implements SamStream<T>{
	
	
	
	private T[] array;
	private int index;
	
	
	
	public ArraySStream(T[] array) {
		super();
		this.array=array;
		index=-1;
	}
	
	
	
	@Override
	public boolean hasSucceed() {
		return index<array.length;
	}
	
	@Override
	public T tryNext() {
		index++;
		if(index<array.length)return array[index];
		else return null;
	}
	
	@Override
	public synchronized void syncNext(Consumer<T> action) {
		if(index<array.length) {
			action.accept(array[index]);
			index++;
		}
	}
	
	@Override
	public synchronized void reset() {
		index=-1;
	}

}
