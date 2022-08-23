package jempasam.samstream;

import java.util.Iterator;

import jempasam.samstream.stream.SamStream;

public interface Streamable<T> extends Iterable<T>{
	SamStream<T> stream();
	default Iterator<T> iterator(){
		stream().reset();
		return stream().iterator();
	}
}
