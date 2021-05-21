package org.usf.jinterval.core;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.usf.jinterval.core.IntervalUtils.requiredPositifDirection;

import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
public final class ImmutableInterval<T extends Comparable<? super T>> implements Interval<T> {
	
	private final T startInclusive;
	private final T endExclusive;
	private final int direction;

	public ImmutableInterval(T start, T exclusifEnd) {
		this.startInclusive = requireNonNull(start);
		this.endExclusive = requireNonNull(exclusifEnd);
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
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof Interval<?> in 
				&& startInclusive.equals(in.startInclusive())
				&& endExclusive.equals(in.endExclusive()));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(startInclusive, endExclusive);
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
