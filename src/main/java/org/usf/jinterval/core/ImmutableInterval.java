package org.usf.jinterval.core;

import static java.util.Objects.requireNonNull;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(exclude = "direction")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ImmutableInterval<T extends Comparable<? super T>> implements Interval<T> {
	
	@Getter private final T start;
	@Getter private final T exclusifEnd;
	private final int direction;
	
	@Override
	public int direction() {
		return direction;
	}
	
	@Override
	public String toString() {
		return Intervals.toString(this);
	}
	
	public static <T extends Comparable<? super T>> ImmutableInterval<T> of(T start, T exclusifEnd){
		
		return new ImmutableInterval<>(requireNonNull(start), requireNonNull(exclusifEnd), Interval.direction(start, exclusifEnd));
	}
}
