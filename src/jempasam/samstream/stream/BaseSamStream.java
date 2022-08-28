package jempasam.samstream.stream;

import java.util.function.Consumer;

public interface BaseSamStream<T>{
	void syncNext(Consumer<T> action);
	T tryNext();
	boolean hasSucceed();
	void reset();
}
