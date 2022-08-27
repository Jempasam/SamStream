package jempasam.samstream.stream;

public class RemainingSamStream<T> implements SamStream<T>{
	
	
	
	private SamStream<T> decorated;
	
	
	
	public RemainingSamStream(SamStream<T> decorated) {
		super();
		this.decorated = decorated;
	}
	
	
	
	@Override
	public void reset() {
		// no reset
	}
	
	@Override
	public T tryNext() {
		return decorated.tryNext();
	}

	@Override
	public boolean hasSucceed() {
		return decorated.hasSucceed();
	}
}
