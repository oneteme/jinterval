package org.usf.jinterval.core;

import java.util.function.BiFunction;

public interface IntervalShifting<T extends Comparable<? super T>> extends Interval<T> {

	default T shiftStart(int v){
		return getFn().apply(getStart(), v);
	}
	default T shiftExclusifEnd(int v){
		return getFn().apply(getExclusifEnd(), v);
	}

	default IntervalShifting<T> shift(int v1, int v2){
		return create(shiftStart(v1), shiftExclusifEnd(v2));
	}
	default IntervalShifting<T> prev(int v1, int v2){
		return create(shiftStart(v1), shiftStart(v2));
	}
	default IntervalShifting<T> next(int v1, int v2){
		return create(shiftExclusifEnd(v1), shiftExclusifEnd(v2));
	}
	
	default IntervalShifting<T> shiftStart(int v1, int v2){
		return create(shiftStart(v1), shiftStart(v2));
	}
	default IntervalShifting<T> shiftExclusifEnd(int v1, int v2){
		return create(shiftExclusifEnd(v1), shiftExclusifEnd(v2));
	}

	IntervalShifting<T> create(T s, T e);
	
	BiFunction<T, Integer, T> getFn();

}
