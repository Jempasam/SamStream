package jempasam.samstream.text;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jempasam.samstream.stream.SamStream;


public class TokenizerSStream implements SamStream<String>{
	
	
	
	public TokenizerConfig config;
	
	private BufferedReader reader;
	private StringBuilder sb;
	private int opener;
	private boolean keeped;
	private int copener;
	private boolean succeed=true;
	
	
	
	public TokenizerSStream(InputStream input, TokenizerConfig config) {
		super();
		this.config=config;
		
		this.reader=new BufferedReader(new InputStreamReader(input));
		this.opener=-1;
		this.copener=-1;
		this.keeped=false;
		this.sb=new StringBuilder();
	}
	
	public TokenizerSStream(String str, TokenizerConfig config) {
		this(new ByteArrayInputStream(str.getBytes()),config);
	}
	
	
	
	@Override
	public String tryNext() {
		if(!succeed)return null;
		try {
			sb.setLength(0);
			int read=-1;
			int previous;
			while(true) {
				previous=read;
				reader.mark(2);
				read=reader.read();
				if(read==-1) {
					if(sb.length()>0) {
						return sb.toString();
					}
					else {
						succeed=false;
						return null;
					}
				}
				else if(opener!=-1) {
					if(read==opener) {
						if(keeped)sb.append((char)read);
						opener=-1;
					}
					else sb.append((char)read);
				}
				else if(copener!=-1) {
					if(read==copener)copener=-1;
				}
				else if(config.escapeChars.indexOf(read)!=-1) {
					sb.append((char)reader.read());
				}
				else if(config.ignoredChars.indexOf(read)!=-1) {
				}
				else if(config.escapeAroundChars.indexOf(read)!=-1) {
					opener=read;
					keeped=false;
				}
				else if(config.keepedEscapeAroundChars.indexOf(read)!=-1) {
					opener=read;
					sb.append((char)read);
					keeped=true;
				}
				else if(config.commentChars.indexOf(read)!=-1) {
					copener=read;
				}
				else if(config.cutChars.indexOf(read)!=-1) {
					if(sb.length()>0)return sb.toString();
				}
				else if(config.startChars.indexOf(read)!=-1) {
					if(sb.length()==0)sb.append((char)read);
					else {
						reader.reset();
						return sb.toString();
					}
				}
				else if(config.endChars.indexOf(read)!=-1) {
					sb.append((char)read);
					return sb.toString();
				}
				else if(config.uniqueChars.indexOf(read)!=-1) {
					if(sb.length()>0) {
						reader.reset();
						return sb.toString();
					}
					else {
						sb.append((char)read);
						return sb.toString();
					}
				}
				else if(config.groupChars.indexOf(read)!=-1) {
					if(sb.length()>0&&read!=previous) {
						reader.reset();
						return sb.toString();
					}
					else {
						sb.append((char)read);
					}
				}
				else if(config.groupChars.indexOf(previous)!=-1) {
					reader.reset();
					return sb.toString();
				}
				else sb.append((char)read);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			succeed=false;
			return null;
		}
	}
	
	@Override
	public boolean hasSucceed() {
		return succeed;
	}
	
}
