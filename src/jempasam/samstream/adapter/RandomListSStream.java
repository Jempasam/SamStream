package jempasam.samstream.adapter;

import java.util.List;
import java.util.Random;

import jempasam.samstream.stream.SamStream;

public class RandomListSStream<T> implements SamStream<T>{
	
	
	
	private List<T> array;
	private Random random;
	
	
	
	public RandomListSStream(List<T> array) {
		super();
		this.array=array;
		this.random=new Random();
	}
	
	@Override
	public boolean hasSucceed() {
		return true;
	}
	
	@Override
	public T tryNext() {
		return this.array.get(this.random.nextInt(this.array.size()));
	}
	
	@Override
	public void reset() {
	}

}
