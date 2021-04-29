package org.usf.jinterval.core;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.usf.jinterval.core.IntervalUtils.requiredPositifDirection;

import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
public final class ImmutableInterval<T extends Comparable<? super T>> implements Interval<T> {
	
	@Getter private final T start;
	@Getter private final T exclusifEnd;
	private final int direction;

	public ImmutableInterval(T start, T exclusifEnd) {
		this.start = requireNonNull(start);
		this.exclusifEnd = requireNonNull(exclusifEnd);
		this.direction = Interval.super.intervalDirection();
	}

	@Override
	public int intervalDirection() {
		return direction;
	}
	
	public ImmutableInterval<T> reverseInterval() {
		return intervalDirection() == 0 
				? this 
				: new ImmutableInterval<>(exclusifEnd, start, -direction);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof Interval<?> in 
				&& start.equals(in.getStart())
				&& exclusifEnd.equals(in.getExclusifEnd()));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(start, exclusifEnd);
	}
	
	@Override
	public String toString() {
		return IntervalUtils.toString(this);
	}
	
	public static <U extends Comparable<? super U>> ImmutableInterval<U>  regularInterval(U start, U exclusifEnd) {
		
		return new ImmutableInterval<>(
				requireNonNull(start), 
				requireNonNull(exclusifEnd), 
				requiredPositifDirection(start, exclusifEnd));
	}
}
