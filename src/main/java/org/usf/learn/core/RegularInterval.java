package org.usf.learn.core;

public interface RegularInterval<T extends Comparable<? super T>> extends Interval<T>{

	default boolean containsField(T temporal) {
		return getStart().compareTo(temporal) <= 0
			&& getExclusifEnd().compareTo(temporal) > 0;
	}
	
	default boolean containsInterval(T start, T exclusifEnd){
		return getStart().compareTo(start) <= 0
			&& getExclusifEnd().compareTo(exclusifEnd) >= 0;
	}

	default boolean intersectInterval(T start, T exclusifEnd){
		return getStart().compareTo(exclusifEnd) < 0
			&& getExclusifEnd().compareTo(start) > 0;
	}
}
