package jempasam.samstream.text;

import java.io.InputStream;

public class TokenizerConfig {
	
	
	
	public String cutChars;
	public String ignoredChars;
	public String uniqueChars;
	public String groupChars;
	public String endChars;
	public String startChars;
	public String escapeAroundChars;
	public String escapeChars;
	public String commentChars;
	public String keepedEscapeAroundChars;
	public boolean keepEmpty;
	
	
	
	public TokenizerConfig() {
		this.cutChars="";
		this.ignoredChars="";
		this.uniqueChars="";
		this.groupChars="";
		this.endChars="";
		this.startChars="";
		this.escapeAroundChars="";
		this.escapeChars="";
		this.commentChars="";
		this.keepedEscapeAroundChars="";
		this.keepEmpty=false;
	}

	public TokenizerConfig(String cutChars, String ignoredChars, String uniqueChars, String groupChars, String endChars,
			String startChars, String escapeAroundChars, String escapeChars, String commentChars, String keepedEscapeAroundChars, boolean keepEmpty) {
		super();
		this.cutChars = cutChars;
		this.ignoredChars = ignoredChars;
		this.uniqueChars = uniqueChars;
		this.groupChars = groupChars;
		this.endChars = endChars;
		this.startChars = startChars;
		this.escapeAroundChars = escapeAroundChars;
		this.escapeChars = escapeChars;
		this.commentChars = commentChars;
		this.keepedEscapeAroundChars = keepedEscapeAroundChars;
		this.keepEmpty=keepEmpty;
	}

	public TokenizerConfig(String cutChars, String uniqueChars, String escapeAroundChars) {
		this();
		this.cutChars = cutChars;
		this.uniqueChars = uniqueChars;
		this.escapeAroundChars = escapeAroundChars;
	}
	
	public TokenizerConfig(TokenizerConfig config) {
		super();
		this.cutChars = config.cutChars;
		this.ignoredChars = config.ignoredChars;
		this.uniqueChars = config.uniqueChars;
		this.groupChars = config.groupChars;
		this.endChars = config.endChars;
		this.startChars = config.startChars;
		this.escapeAroundChars = config.escapeAroundChars;
		this.escapeChars = config.escapeChars;
		this.commentChars = config.commentChars;
		this.keepedEscapeAroundChars= config.keepedEscapeAroundChars;
		this.keepEmpty=config.keepEmpty;
	}
	
	
	
	public TokenizerConfig setSpliter(String cutChars) {
		this.cutChars=cutChars;
		return this;
	}
	
	public TokenizerConfig setIgnored(String ignoredChars) {
		this.ignoredChars=ignoredChars;
		return this;
	}
	
	public TokenizerConfig setAlone(String uniqueChars) {
		this.uniqueChars=uniqueChars;
		return this;
	}
	
	public TokenizerConfig setGroupAlone(String groupChars) {
		this.groupChars=groupChars;
		return this;
	}
	
	public TokenizerConfig setLastOfToken(String endChars) {
		this.endChars=endChars;
		return this;
	}
	
	public TokenizerConfig setFirstOfToken(String startChars) {
		this.startChars=startChars;
		return this;
	}
	
	public TokenizerConfig setStringEscaper(String escapeAroundChars) {
		this.escapeAroundChars=escapeAroundChars;
		return this;
	}
	
	public TokenizerConfig setEscaper(String escapeChars) {
		this.escapeChars=escapeChars;
		return this;
	}
	
	public TokenizerConfig setCommenter(String commentChars) {
		this.commentChars=commentChars;
		return this;
	}
	
	public TokenizerConfig setStringEscaperKeeped(String keepedEscapeAroundChars) {
		this.keepedEscapeAroundChars=keepedEscapeAroundChars;
		return this;
	}
	
	public TokenizerConfig keepEmptyToken() {
		this.keepEmpty=true;
		return this;
	}
	
	public TokenizerConfig notKeepEmptyToken() {
		this.keepEmpty=false;
		return this;
	}
	
	public TokenizerSStream create(InputStream input) {
		return new TokenizerSStream(input, this);
	}
}
