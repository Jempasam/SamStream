package jempasam.samstream.adapter;

import java.util.List;
import java.util.function.Consumer;

import jempasam.samstream.stream.SamStream;

public class ListSStream<T> implements SamStream<T>{
	
	
	
	private List<T> array;
	private int index;
	private int start;
	
	
	
	public ListSStream(List<T> array, int start) {
		super();
		this.array=array;
		this.start=start-1;
		index=this.start;
	}
	
	
	
	@Override
	public boolean hasSucceed() {
		return index<array.size();
	}
	
	@Override
	public T tryNext() {
		index++;
		if(index<array.size())return array.get(index);
		else return null;
	}
	
	@Override
	public synchronized void syncNext(Consumer<T> action) {
		if(index<array.size()) {
			action.accept(array.get(index));
			index++;
		}
	}
	
	@Override
	public synchronized void reset() {
		index=this.start;
	}

}
