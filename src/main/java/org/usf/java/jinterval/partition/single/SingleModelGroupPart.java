package org.usf.java.jinterval.partition.single;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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
	
	<T, R> R apply(List<T> list, Function<M, R> identity, BiFunction<R, T, R> fn) {
		
		return apply(list, identity.apply(model), fn);
	}

	<T, U, R> R apply(List<T> list, U identity, BiFunction<U, T, U> fn, BiFunction<M, U, R> finisher) {
		
		return finisher.apply(model, apply(list, identity, fn));
	}
	
	private <T, R> R apply(List<T> list, R identity, BiFunction<R, T, R> fn) {
		
		R acc = identity;
		for(int[] pair : partitions) {
			for(int i=pair[0]; i<pair[1]; i++) {
				acc = fn.apply(acc, list.get(i));
			}
		}
		return acc;
	}
	
	@Override
	public String toString() {
		return model + " : " + Arrays.toString(partitions);
	}
	
	public static <M> SingleModelGroupPart<M> emptyPartition(M model) {
		return new SingleModelGroupPart<>(model, EMPTY_PARTITION);
	}
	
}