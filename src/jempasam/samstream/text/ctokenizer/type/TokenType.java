package jempasam.samstream.text.ctokenizer.type;

public interface TokenType {
	int maxSize();
	boolean test(String tested);
}
