package org.usf.jinterval.core;

import java.util.function.BiFunction;
import java.util.function.IntSupplier;

public interface Interval<T extends Comparable<? super T>> {
	
	T getStart();

	T getExclusifEnd();
	
	default boolean containsField(T temporal) {

		int diff = direction();
		return diff == 0 || (diff > 0 
				? getStart().compareTo(temporal) <= 0 && getExclusifEnd().compareTo(temporal) > 0
				: getStart().compareTo(temporal) <= 0 || getExclusifEnd().compareTo(temporal) > 0);
	}
	
	default boolean containsInterval(Interval<T> p) {

		return containsInterval(p.getStart(), p.getExclusifEnd(), p::direction);
	}
	
	default boolean containsInterval(T start, T exclusifEnd) {

		return containsInterval(start, exclusifEnd, ()-> Intervals.direction(start, exclusifEnd));
	}

	private boolean containsInterval(T start, T exclusifEnd, IntSupplier dirSupp) {
		int diff = direction();
		if(diff == 0) {
			return true;
		}
		int pDiff = dirSupp.getAsInt();
		if(pDiff == 0 || pDiff < 0 && diff > 0) {
			return false;
		}
		return (diff ^ pDiff) >= 0
				? getStart().compareTo(start) <= 0 && getExclusifEnd().compareTo(exclusifEnd) >= 0
				: getStart().compareTo(start) <= 0 || getExclusifEnd().compareTo(exclusifEnd) >= 0;
	}
	
	default boolean intersectInterval(Interval<T> p) {

		return intersectInterval(p.getStart(), p.getExclusifEnd(), p::direction);
	}
	
	default boolean intersectInterval(T start, T exclusifEnd) {

		return intersectInterval(start, exclusifEnd, ()-> Intervals.direction(start, exclusifEnd));
	}

	private boolean intersectInterval(T start, T exclusifEnd, IntSupplier dirSupp) {
		int diff = direction();
		if(diff == 0) {
			return true;
		}
		int pDiff = dirSupp.getAsInt();
		if(pDiff == 0 || pDiff < 0 && diff < 0) {
			return true;
		}
		return (diff ^ pDiff) >= 0
				? getStart().compareTo(exclusifEnd) < 0 && getExclusifEnd().compareTo(start) > 0
				: getStart().compareTo(exclusifEnd) < 0 || getExclusifEnd().compareTo(start) > 0;
	}
	
	default boolean isInverted() {
		return direction() <= 0;
	}
	
	default boolean isRegular() {
		return direction() > 0;
	}

	default <I extends Interval<T>> I reverseInterval(BiFunction<T, T, I> fn) {
		return fn.apply(getExclusifEnd(), getStart());
	}
	
	default boolean reverseOf(Interval<T> interval) {
		return Intervals.symmetric(this, interval);
	}
	
	default int direction() {
		return Intervals.direction(getStart(), getExclusifEnd());
	}
}
