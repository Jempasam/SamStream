package jempasam.samstream.text.ctokenizer.action;

import java.io.BufferedReader;

public interface TokenizerAction {
	boolean act(BufferedReader reader, StringBuilder builder);
}
