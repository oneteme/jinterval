package org.usf.jinterval.core;

import static org.usf.jinterval.core.Intervals.max;
import static org.usf.jinterval.core.Intervals.min;
import static org.usf.jinterval.util.Asserts.requirePositif;

import java.util.Optional;
import java.util.function.BiFunction;

public interface RegularInterval<T extends Comparable<? super T>> extends Interval<T> {

	@Override
	default <I extends Interval<T>> I reverseInterval(BiFunction<? super T, ? super T, I> fn) {
		throw new UnsupportedOperationException("not supported in a regular interval");
	}

	@Override
	default boolean symmetrical(Interval<T> interval) {
		throw new UnsupportedOperationException("not supported in a regular interval");
	}
	
	default Optional<Interval<T>> intervalIntersection(RegularInterval<T> inverval) {
		
		return intervalIntersection(inverval.getStart(), inverval.getExclusifEnd());
	}

	default Optional<Interval<T>> intervalIntersection(T start, T exclusifEnd) { //assert direction > 0
		requiredRegularInterval(start, exclusifEnd);
		T s = max(getStart(), start);
		T e = min(getExclusifEnd(), exclusifEnd);
		return s.compareTo(e)>=0 
				? Optional.empty() 
				: Optional.of(ImmutableInterval.of(s, e));
	}

	@Override
	default int direction() {
		return 1;
	}
	
	static <T extends Comparable<? super T>> int requiredRegularInterval(T start, T exclusifEnd) {
		
		return requirePositif(Interval.direction(start, exclusifEnd), ()-> "regular interval is required");
	}
}
