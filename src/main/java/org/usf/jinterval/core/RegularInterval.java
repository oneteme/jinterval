package org.usf.jinterval.core;

import static org.usf.jinterval.core.Intervals.max;
import static org.usf.jinterval.core.Intervals.min;

import java.util.Optional;

public interface RegularInterval<T extends Comparable<? super T>> extends Interval<T> {

	@Override
	default int direction() {
		return 1;
	}

	default Optional<Interval<T>> intervalIntersection(T start, T exclusifEnd) {
		T s = max(getStart(), start);
		T e = min(getExclusifEnd(), exclusifEnd);
		return s.compareTo(e)>=0 
				? Optional.empty() 
				: Optional.of(ImmutableInterval.of(s, e)) ;
	}
	
}
