package jempasam.samstream.text.ctokenizer.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jempasam.samstream.SamStreams;

public class SimpleTokenType implements TokenType{
	
	
	
	private Map<Character,Set<String>> chunks; 
	private int maxsize;
	
	
	
	public SimpleTokenType() {
		chunks=new HashMap<>();
		maxsize=0;
	}
	
	public SimpleTokenType(Iterable<String> strs) {
		this();
		for(String str : strs)add(str);
	}
	
	public SimpleTokenType(String ...strs) {
		this(SamStreams.create(strs));
	}
	
	
	
	public void add(String chunk) {
		Character firstChar=chunk.charAt(0);
		Set<String> chunksSet=chunks.get(firstChar);
		if(chunksSet==null) {
			chunksSet=new HashSet<>();
			chunks.put(firstChar, chunksSet);
		}
		chunksSet.add(chunk);
		maxsize=Math.max(chunk.length(), maxsize);
	}
	
	public boolean test(String tested){
		Set<String> list=chunks.get(tested.charAt(0));
		if(list!=null) {
			for(String str: list) {
				if(tested.startsWith(str))return true;
			}
		}
		return false;
	}
	
	public int maxSize() {
		return maxsize;
	}
}
