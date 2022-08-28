package jempasam.samstream.stream;

import java.util.function.Consumer;

public abstract class AbstractSamStream<T> implements SamStream<T>{
	
	@Override
	public synchronized void syncNext(Consumer<T> action) {
		T v=tryNext();
		if(hasSucceed())action.accept(v);
	}
}
