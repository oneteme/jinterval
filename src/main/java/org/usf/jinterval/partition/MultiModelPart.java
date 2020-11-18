package org.usf.jinterval.partition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MultiModelPart<I, T, U> {

	private final List<T> data;
	private final int[] indexs;
	private final int startIndex;
	private final int exclusifEndIndex;
	@Getter private final I start;
	@Getter private final I exclusifEnd;
	private final ObjIntFunction<T, U> subList;

	public List<U> apply(BinaryOperator<U> op) {
		return apply(op, ArrayList::new);
	}

	public List<U> apply(BinaryOperator<U> op, IntFunction<List<U>> supplier) {
		List<U> list = supplier.apply(exclusifEndIndex - startIndex);
		accept(op, list::add);
		return list;
	}
	
	public <R> List<R> apply(R identity, BiFunction<R, U, R> fn) {
		return apply(identity, fn, ArrayList::new);
	}

	public <R> List<R> apply(R identity, BiFunction<R, U, R> fn, IntFunction<List<R>> supplier) {
		List<R> list = supplier.apply(exclusifEndIndex - startIndex);
		accept(identity, fn, list::add);
		return list;
	}
	
	public void accept(BinaryOperator<U> op, Consumer<U> consumer) {
		accept(1, subList, op, consumer);
	}
	
	public <R> void accept(R identity, BiFunction<R, U, R> fn, Consumer<R> consumer) {
		accept(0, (o, i)-> identity, fn, consumer);
	}

	private <R> void accept(int first, ObjIntFunction<T, R> identity, BiFunction<R, U, R> fn, Consumer<R> consumer) {
		if(first < indexs.length) {
			for(int i=startIndex; i<exclusifEndIndex; i++) {
				R res = identity.get(data.get(indexs[0]), i);
				for(int j=first; j<indexs.length; j++) {
					res = fn.apply(res, subList.get(data.get(indexs[j]), i));
				}
				consumer.accept(res);
			}
		}
	}
	
	public List<T> models(){
		return IntStream.of(indexs)
				.mapToObj(data::get)
				.collect(Collectors.toList());
	}
	
	public int modelCount() {
		return indexs.length;
	}
	
	@Override
	public String toString() {
		return "[" + start + "-"+ exclusifEnd + "| : " + Arrays.toString(indexs);
	}

	@FunctionalInterface
	public interface ObjIntFunction<T, U> {
		
		U get(T obj, int index);
	}
}
