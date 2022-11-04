package jempasam.samstream.collectors;

import java.util.ArrayList;
import java.util.List;

public class SamCollectors {
	
	
	
	private SamCollectors() {}
	
	
	
	public static <O,I extends O> SamCollector<I, List<O>, List<O>> toList(){
		return new SamCollector<I, List<O>, List<O>>() {
			
			private int count=0;
			private List<O> list=new ArrayList<>();
			
			@Override
			public synchronized void give(I ingredient) {
				list.add(ingredient);
				count++;
			}
			
			@Override
			public synchronized int countIngredient() {
				return count;
			}
			
			@Override
			public synchronized List<O> getState() {
				return list;
			}
			
			@Override
			public synchronized List<O> getResult() {
				return list;
			}
		};
	}
	
	public static <O,I extends O> SamCollector<I, List<O>, List<O>> toList(O separator){
		return new SamCollector<I, List<O>, List<O>>() {
			
			private int count=0;
			private List<O> list=new ArrayList<>();
			
			@Override
			public synchronized void give(I ingredient) {
				list.add(ingredient);
				list.add(separator);
				count++;
			}
			
			@Override
			public synchronized int countIngredient() {
				return count;
			}
			
			@Override
			public synchronized List<O> getState() {
				return list;
			}
			
			@Override
			public synchronized List<O> getResult() {
				list.remove(list.size()-1);
				return list;
			}
		};
	}
	
	public static SamCollector<String, StringBuilder, String> concatenate(String separator){
		return new SamCollector<String, StringBuilder, String>() {
			
			private StringBuilder sb=new StringBuilder();
			private int count;
			
			@Override
			public synchronized void give(String ingredient) {
				if(count>0)sb.append(separator);
				sb.append(ingredient);
				count++;
			}
			
			@Override
			public synchronized int countIngredient() {
				return count;
			}
			
			@Override
			public synchronized StringBuilder getState() {
				return sb;
			}
			
			@Override
			public synchronized String getResult() {
				return sb.toString();
			}
		};
	}
	
	public static <T> SamCollector<Number, Number, Number>  summing(){
		return new SamCollector<Number, Number, Number>() {
			
			private long num=0;
			private int count;
			
			@Override
			public synchronized void give(Number ingredient) {
				num+=ingredient.longValue();
				count++;
			}
			
			@Override
			public synchronized int countIngredient() {
				return count;
			}
			
			@Override
			public synchronized Number getState() {
				return num;
			}
			
			@Override
			public synchronized Number getResult() {
				return num;
			}
		};
	}
	
	
	
}
