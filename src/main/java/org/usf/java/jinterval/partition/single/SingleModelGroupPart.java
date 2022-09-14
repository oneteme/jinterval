package org.usf.java.jinterval.partition.single;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 *
 */
@Getter
@RequiredArgsConstructor 
public final class SingleModelGroupPart<M> {
	
	private static final int[][] EMPTY_PARTITION = new int[0][0];
	
	private final M model;
	private final int[][] partitions;
	
	<T, R> R apply(List<T> list, R identity, BiFunction<R, T, R> fn) {
		return apply(0, list, identity, fn);
	}
	
	<T, R> R apply(int shift, List<T> list, R identity, BiFunction<R, T, R> fn) {
		R acc = identity;
		int beg = 0;
		if(shift < 0) {
			beg = -shift;
			shift = 0;
		}
		for(int[] pair : partitions) {
			for(int i=max(shift+pair[0], beg); i<min(shift+pair[1], list.size()); i++) {
				acc = fn.apply(acc, list.get(i));
			}
		}
		return acc;
	}
	
	@Override
	public String toString() {
		return model + " : " + Stream.of(partitions)
			.map(arr-> '[' + arr[0] + "-" + arr[1] + ']')
			.collect(joining(" "));
	}
	
	public static <M> SingleModelGroupPart<M> emptyPartition(M model) {
		return new SingleModelGroupPart<>(model, EMPTY_PARTITION);
	}
	
}