package org.usf.jinterval.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class ImmutableCycliqueInterval<T extends Comparable<? super T>> implements CyclicInterval<T> {
	
	private final T start;
	private final T exclusifEnd;
	
	@Override
	public String toString() {
		return Intervals.toString(this);
	}

}
