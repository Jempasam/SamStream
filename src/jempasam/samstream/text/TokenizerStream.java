package jempasam.samstream.text;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jempasam.samstream.stream.SamStream;

public class TokenizerStream implements SamStream<String>{
	
	
	
	public TokenizerConfig config;
	
	private BufferedReader reader;
	private StringBuilder sb;
	private int depth;
	private int opener;
	private int copener;
	private boolean succeed=true;
	
	
	
	public TokenizerStream(InputStream input, TokenizerConfig config) {
		super();
		this.config=config;
		
		this.reader=new BufferedReader(new InputStreamReader(input));
		this.depth=0;
		this.opener=-1;
		this.copener=-1;
		this.sb=new StringBuilder();
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
					if(read==opener)opener=-1;
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
