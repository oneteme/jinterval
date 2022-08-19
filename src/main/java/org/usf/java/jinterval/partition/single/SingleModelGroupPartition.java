package org.usf.java.jinterval.partition.single;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.usf.java.jinterval.core.Interval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 *
 */
@Getter
@RequiredArgsConstructor
public final class SingleModelGroupPartition<M> implements Interval<Instant> {
	
	private final Instant start;
	private final Instant endExclusive;
	private final int secondStep;
	private final List<SingleModelGroupPart<M>> partitions;

	<T, R> List<R> apply(List<T> list, Function<M, R> identity, BiFunction<R, T, R> fn) {

		return partitions.parallelStream()
			.map(p-> p.apply(list, identity, fn))
			.sequential()
			.collect(toList());
	}

	<T, U, R> List<R> apply(List<T> list, U identity, BiFunction<U, T, U> fn, BiFunction<M, U, R> finisher) {
		
		return partitions.parallelStream()
			.map(p-> p.apply(list, identity, fn, finisher))
			.sequential()
			.collect(toList());
	}
	
	@Override
	public Instant startInclusive() {
		return start;
	}
	
	@Override
	public Instant endExclusive() {
		return endExclusive;
	}
	
	public int secondStep() {
		return secondStep;
	}
	
}
