package jempasam.samstream.functional;

@FunctionalInterface
public interface ThrowConsumer<T> {
	void accept(T e) throws Exception;
}
