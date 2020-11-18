package org.usf.jinterval.core;

import java.util.Optional;

public interface Interval<T> {

	T getStart();

	T getExclusifEnd();
	
	boolean containsField(T field);

	boolean containsInterval(T start, T exclusifEnd);
	
	boolean intersectInterval(T start, T exclusifEnd);

	Optional<Interval<T>> intervalIntersection(T start, T exclusifEnd);
	
	default boolean containsInterval(Interval<T> p){
		return containsInterval(p.getStart(), p.getExclusifEnd());
	}

	default boolean intersectInterval(Interval<T> p){
		return intersectInterval(p.getStart(), p.getExclusifEnd());
	}
	
	default Optional<Interval<T>> intervalIntersection(Interval<T> p){
		return intervalIntersection(p.getStart(), p.getExclusifEnd());
	}
}
