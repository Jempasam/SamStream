package jempasam.samstream.collectors;

public interface SamCollector<I,M,O> {
	void give(I ingredient);
	int countIngredient();
	O getResult();
	M getState();
}
