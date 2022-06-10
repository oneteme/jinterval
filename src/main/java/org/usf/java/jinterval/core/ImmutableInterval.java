package org.usf.java.jinterval.core;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.usf.java.jinterval.core.IntervalUtils.requiredPositifDirection;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(of = {"startInclusive", "endExclusive"})
@RequiredArgsConstructor(access = PRIVATE)
public final class ImmutableInterval<T extends Comparable<? super T>> implements Interval<T> {
	
	private final T startInclusive;
	private final T endExclusive;
	private final int direction;

	public ImmutableInterval(T start, T endExclusive) {
		this.startInclusive = requireNonNull(start);
		this.endExclusive = requireNonNull(endExclusive);
		this.direction = Interval.super.intervalDirection();
	}
	
	@Override
	public T startInclusive() {
		return startInclusive;
	}
	
	@Override
	public T endExclusive() {
		return endExclusive;
	}

	@Override
	public int intervalDirection() {
		return direction;
	}
	
	public ImmutableInterval<T> reverseInterval() {
		return intervalDirection() == 0 
				? this 
				: new ImmutableInterval<>(endExclusive, startInclusive, -direction);
	}

	@Override
	public String toString() {
		return IntervalUtils.toString(this);
	}
	
	public static <U extends Comparable<? super U>> ImmutableInterval<U>  regularInterval(U startInclusive, U endExclusive) {
		
		return new ImmutableInterval<>(
				requireNonNull(startInclusive), 
				requireNonNull(endExclusive), 
				requiredPositifDirection(startInclusive, endExclusive));
	}
}
