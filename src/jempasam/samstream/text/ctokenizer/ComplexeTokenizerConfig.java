package jempasam.samstream.text.ctokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jempasam.samstream.text.ctokenizer.action.TokenizerAction;
import jempasam.samstream.text.ctokenizer.type.TokenType;

public class ComplexeTokenizerConfig {
	
	
	
	private List<TokenizerTransition> transitions;
	
	
	
	public ComplexeTokenizerConfig() {
		transitions=new ArrayList<>();
	}
	
	
	
	public void add(Predicate<TokenType> type, Predicate<Integer> group, List<TokenizerAction> actions) {
		TokenizerTransition tr=new TokenizerTransition();
		tr.type=type;
		tr.group=group;
		tr.actions=actions;
		transitions.add(tr);
	}
	
	
	
	
	private class TokenizerTransition{
		Predicate<TokenType> type;
		Predicate<Integer> group;
		List<TokenizerAction> actions;
	}
}
