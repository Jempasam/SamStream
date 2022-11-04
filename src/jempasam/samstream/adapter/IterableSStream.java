package jempasam.samstream.adapter;

import java.util.Iterator;

import jempasam.samstream.stream.SamStream;

public class IterableSStream<T> implements SamStream<T>{
	
	
	
	private Iterable<T> iterable;
	private Iterator<T> iterator;
	private boolean succeed=true;
	
	
	
	public IterableSStream(Iterable<T> iterable) {
		super();
		this.iterable = iterable;
		reset();
	};
	
	@Override
	public void reset() {
		this.iterator=iterable.iterator();
	}
	
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
