package jempasam.samstream.text.ctokenizer.typepred;

import java.util.Set;
import java.util.function.Predicate;

public class SetPredicate<T> implements Predicate<T>{
	
	
	
	private Set<T> types;
	
	
	
	public SetPredicate(Set<T> types) {
		this.types=types;
	}
	
	
	
	public boolean test(T type) {
		return types.contains(type);
	}
}
