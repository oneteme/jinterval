package org.usf.java.jinterval.partition.single;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.usf.java.jinterval.core.TemporalUtils.nStepBetween;

import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.usf.java.jinterval.core.Curve;
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

	public <T, R> List<R> apply(List<T> list, Function<M, R> identity, BiFunction<R, T, R> fn) {
		return partitions.stream()
			.map(p-> p.apply(list, identity.apply(p.getModel()), fn))
			.collect(toList());
	}

	public <T, U, R> List<R> apply(List<T> list, U identity, BiFunction<U, T, U> fn, BiFunction<M, U, R> finisher) {
		return partitions.stream()
			.map(p-> finisher.apply(p.getModel(), p.apply(list, identity, fn)))
			.collect(toList());
	}
	
	public <T, R> List<R> apply(Curve<T> curve, Function<M, R> identity, BiFunction<R, T, R> fn) {
		var shift = shiftIndex(curve); //can be negative
		return partitions.stream()
			.map(p-> p.apply(shift, curve.points(), identity.apply(p.getModel()), fn))
			.collect(toList());
	}

	public <T, U, R> List<R> apply(Curve<T> curve, U identity, BiFunction<U, T, U> fn, BiFunction<M, U, R> finisher) {
		var shift = shiftIndex(curve); //can be negative
		return partitions.stream()
			.map(p-> finisher.apply(p.getModel(), p.apply(shift, curve.points(), identity, fn)))
			.collect(toList());
	}
	
	private final int shiftIndex(Curve<?> curve) {
		if(curve.secondStep() != secondStep()) {
			throw new IllegalArgumentException(format("partition.step[%s] <> curve.step[%s]", secondStep(), curve.secondStep()));
		}
		return (int) nStepBetween(curve.startInclusive(), this.startInclusive(), secondStep, SECONDS);
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
	
	public static final <M> SingleModelGroupPartition<M> of(M model, Interval<Instant> interval, int secondStep) {
		
		var exEnd = (int)nStepBetween(interval.startInclusive(), interval.endExclusive(), secondStep, SECONDS);
		return new SingleModelGroupPartition<>(interval.startInclusive(), interval.endExclusive(), secondStep, 
				singletonList(new SingleModelGroupPart<>(model, new int[][] {{0, exEnd}})));
	}
	
}
