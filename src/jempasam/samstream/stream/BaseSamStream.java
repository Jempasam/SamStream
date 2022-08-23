package jempasam.samstream.stream;

public interface BaseSamStream<T>{
	T tryNext();
	boolean hasSucceed();
	void reset();
}
