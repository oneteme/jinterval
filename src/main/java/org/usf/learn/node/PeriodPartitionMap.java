package org.usf.learn.node;

import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class PeriodPartitionMap<M> {
	
	private final Instant start;
	private final Instant exclusifEnd;
	private final List<PeriodPartition<M>> partitions;

	<T, R> List<R> apply(List<T> list, Function<M, R> identity, BiFunction<R, T, R> fn) {

		return partitions.parallelStream()
			.map(p-> p.apply(list, identity, fn))
			.sequential()
			.collect(Collectors.toList());
	}

	<T, U, R> List<R> apply(List<T> list, U identity, BiFunction<U, T, U> fn, BiFunction<M, U, R> finisher) {
		
		return partitions.parallelStream()
			.map(p-> p.apply(list, identity, fn, finisher))
			.sequential()
			.collect(Collectors.toList());
	}
	
}
