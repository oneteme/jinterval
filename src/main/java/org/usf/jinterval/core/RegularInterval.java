package org.usf.jinterval.core;

import static org.usf.jinterval.core.Intervals.max;
import static org.usf.jinterval.core.Intervals.min;

import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

public interface RegularInterval<T extends Comparable<? super T>> extends Interval<T> {

	@Override
	default boolean containsField(T obj) {
		return getStart().compareTo(obj) <= 0
			&& getExclusifEnd().compareTo(obj) > 0;
	}

	@Override
	default boolean containsInterval(T start, T exclusifEnd){
		return getStart().compareTo(start) <= 0
			&& getExclusifEnd().compareTo(exclusifEnd) >= 0;
	}

	@Override
	default boolean  intersectInterval(T start, T exclusifEnd){
		return getStart().compareTo(exclusifEnd) < 0
			&& getExclusifEnd().compareTo(start) > 0;
	}
	
	@Override
	default Optional<Interval<T>> intervalIntersection(T start, T exclusifEnd) {
		T s = max(getStart(), start);
		T e = min(getExclusifEnd(), exclusifEnd);
		return s.compareTo(e)>=0 
				? Optional.empty() 
				: Optional.of(new ImmutableRegularInterval<>(s, e)) ;
	}
	
}
