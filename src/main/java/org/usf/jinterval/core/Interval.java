package org.usf.jinterval.core;

import java.util.function.IntSupplier;

public interface Interval<T extends Comparable<? super T>> {
	
	T getStart();

	T getExclusifEnd();
	
	default int direction() {
		return direction(getStart(), getExclusifEnd());
	}
	
	default boolean containsField(T temporal) {

		int diff = direction();
		return diff == 0 || diff > 0 
				? getStart().compareTo(temporal) <= 0 && getExclusifEnd().compareTo(temporal) > 0
				: getStart().compareTo(temporal) <= 0 || getExclusifEnd().compareTo(temporal) > 0;
	}
	
	default boolean containsInterval(Interval<T> p) {

		return containsInterval(p.getStart(), p.getExclusifEnd(), p::direction);
	}
	
	default boolean containsInterval(T start, T exclusifEnd) {

		return containsInterval(start, exclusifEnd, ()-> direction(start, exclusifEnd));
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

		return intersectInterval(start, exclusifEnd, ()-> direction(start, exclusifEnd));
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
	
	static <T extends Comparable<? super T>> int direction(T start, T exclusifEnd) {
		
		return exclusifEnd.compareTo(start);
	}
}
