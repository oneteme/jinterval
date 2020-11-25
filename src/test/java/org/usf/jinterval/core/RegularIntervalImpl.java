package org.usf.jinterval.core;

import java.util.function.BiFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
final class RegularIntervalImpl<T extends Comparable<? super T>> implements IntervalShifting<T>, RegularInterval<T> {

	private final T start;
	private final T exclusifEnd;
	private final BiFunction<T, Integer, T> fn;
	
	@Override
	public RegularIntervalImpl<T> create(T s, T e){
		return new RegularIntervalImpl<>(s, e, fn);
	}
	
	@Override
	public boolean equals(Object obj) {
		return Intervals.equals(this, (Interval<?>) obj);
	}
	
	@Override
	public String toString() {
		return Intervals.toString(this);
	}
	
	@Override //important
	public int direction() {
		return RegularInterval.super.direction();
	}
	
}