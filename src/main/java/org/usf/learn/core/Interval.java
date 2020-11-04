package org.usf.learn.core;

public interface Interval<T extends Comparable<? super T>> {
	
	T getStart();
	
	T getExclusifEnd();
	
	default boolean containsInterval(Interval<T> p){
		return containsInterval(p.getStart(), p.getExclusifEnd());
	}
	
	default boolean containsInterval(T start, T exclusifEnd){
		return getStart().compareTo(start) <= 0
			&& getExclusifEnd().compareTo(exclusifEnd) >= 0;
	}

	default boolean intersectInterval(Interval<T> p){
		return intersectInterval(p.getStart(), p.getExclusifEnd());
	}

	default boolean intersectInterval(T start, T exclusifEnd){
		return getStart().compareTo(exclusifEnd) < 0
			&& getExclusifEnd().compareTo(start) > 0;
	}
	
	default boolean containsField(T temporal) {
		return getStart().compareTo(temporal) <= 0
			&& getExclusifEnd().compareTo(temporal) > 0;
	}
}
