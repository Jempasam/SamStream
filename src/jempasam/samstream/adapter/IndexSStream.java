package jempasam.samstream.adapter;

import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import jempasam.samstream.stream.SamStream;

public class IndexSStream<T> implements SamStream<T>{
	
	
	
	private IntFunction<T> next;
	private IntPredicate hasSucceed;
	private boolean succeed;
	private int index;
	
	
	
	public IndexSStream(IntPredicate hasSucceed, IntFunction<T> next) {
		super();
		this.next = next;
		this.hasSucceed = hasSucceed;
		reset();
	}

	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
	@Override
	public T tryNext() {
		index++;
		if(hasSucceed.test(index)) {
			return next.apply(index);
		}
		else {
			succeed=false;
			return null;
		}
	}
	
	@Override
	public void reset() {
		this.succeed=true;
		this.index=-1;
	}

}
