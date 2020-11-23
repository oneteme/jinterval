package org.usf.jinterval.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class ImmutableRegularInterval<T extends Comparable<? super T>> implements RegularInterval<T> {
	
	private final T start;
	private final T exclusifEnd;
	
	@Override
	public String toString() {
		return Intervals.toString(this);
	}

}
