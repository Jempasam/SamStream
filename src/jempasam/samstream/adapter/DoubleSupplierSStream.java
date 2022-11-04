package jempasam.samstream.adapter;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import jempasam.samstream.stream.SamStream;

public class DoubleSupplierSStream<T> implements SamStream<T>{
	
	
	
	private Supplier<T> next;
	private BooleanSupplier hasNext;
	private boolean succeed;
	
	
	
	public DoubleSupplierSStream(Supplier<T> next, BooleanSupplier hasNext) {
		super();
		this.next = next;
		this.hasNext = hasNext;
		succeed=true;
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
	@Override
	public T tryNext() {
		if(this.hasNext.getAsBoolean()) {
			return this.next.get();
		}
		else {
			succeed=false;
			return null;
		}
	}

}
