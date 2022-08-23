package jempasam.samstream.adapter;

import java.util.Iterator;

import jempasam.samstream.stream.SamStream;

public class IteratorSStream<T> implements SamStream<T>{
	
	
	
	private Iterator<T> iterator;
	private boolean succeed=true;
	
	
	
	public IteratorSStream(Iterator<T> iterator) {
		super();
		this.iterator = iterator;
		reset();
	};
	
	
	
	@Override
	public T tryNext() {
		if(iterator.hasNext())return iterator.next();
		else {
			succeed=false;
			return null;
		}
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
	
	

}
