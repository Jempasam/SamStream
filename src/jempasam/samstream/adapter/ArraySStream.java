package jempasam.samstream.adapter;

import java.util.function.Consumer;
import java.util.function.Function;
import jempasam.samstream.stream.SamStream;

public class ArraySStream<T> implements SamStream<T>{
	
	
	
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
	public void reset() {
		index=-1;
	}

}
