package jempasam.samstream.text;

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
	}



	public TokenizerConfig(String cutChars, String ignoredChars, String uniqueChars, String groupChars, String endChars,
			String startChars, String escapeAroundChars, String escapeChars, String commentChars) {
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
	}
	
	
	
}
