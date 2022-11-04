package jempasam.samstream.adapter;

import java.util.function.Supplier;

import jempasam.samstream.stream.SamStream;

public class UniqueSupplierSStream<T> implements SamStream<T>{
	
	
	
	private Supplier<T> tryNext;
	private boolean succeed;
	
	
	
	public UniqueSupplierSStream(Supplier<T> tryNext) {
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
		T ret=tryNext.get();
		if(ret==null) {
			succeed=false;
			return null;
		}
		else return ret;
	}
}
