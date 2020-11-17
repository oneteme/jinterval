package org.usf.jpartition.core;

import java.util.function.BiFunction;

import lombok.Getter;

@Getter
class IntervalImpl<T extends Comparable<? super T>> extends ImmutableInterval<T> {

	private final BiFunction<T, Integer, T> fn;
	
	public IntervalImpl(T start, T exclusifEnd, BiFunction<T, Integer, T> fn) {
		super(start, exclusifEnd);
		this.fn = fn;
	}
	
	final T shiftStart(int v){
		return shiftItem(getStart(), v);
	}
	final T shiftExclusifEnd(int v){
		return shiftItem(getExclusifEnd(), v);
	}

	final IntervalImpl<T> shift(int v1, int v2){
		return create(shiftStart(v1), shiftExclusifEnd(v2));
	}
	final IntervalImpl<T> prev(int v1, int v2){
		return create(shiftStart(v1), shiftStart(v2));
	}
	final IntervalImpl<T> next(int v1, int v2){
		return create(shiftExclusifEnd(v1), shiftExclusifEnd(v2));
	}
	
	final IntervalImpl<T> shiftStart(int v1, int v2){
		return create(shiftStart(v1), shiftStart(v2));
	}
	final IntervalImpl<T> shiftExclusifEnd(int v1, int v2){
		return create(shiftExclusifEnd(v1), shiftExclusifEnd(v2));
	}

	IntervalImpl<T> create(T s, T e){
		return new IntervalImpl<>(s, e, fn);
	}
	
	T shiftItem(T obj, int value){
		return fn.apply(obj, value);
	}
	
	@Override 
	public String toString() {
		return "[" + getStart() + ", " + getExclusifEnd() + "[";
	}
}