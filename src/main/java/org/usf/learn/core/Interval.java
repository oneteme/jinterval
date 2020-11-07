package org.usf.learn.core;

public interface Interval<T> {

	T getStart();

	T getExclusifEnd();
	
	boolean containsField(T field);

	boolean containsInterval(T start, T exclusifEnd);
	
	boolean intersectInterval(T start, T exclusifEnd);
	
	default boolean containsInterval(Interval<T> p){
		return containsInterval(p.getStart(), p.getExclusifEnd());
	}

	default boolean intersectInterval(Interval<T> p){
		return intersectInterval(p.getStart(), p.getExclusifEnd());
	}
}
