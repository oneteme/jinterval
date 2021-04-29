package org.usf.jinterval.core;

import static java.util.Objects.requireNonNull;
import static org.usf.jinterval.core.IntervalUtils.direction;

import java.util.function.IntSupplier;

public interface Interval<T extends Comparable<? super T>> {
	
	T getStart();

	T getExclusifEnd();
	
	default boolean containsField(T field) {
		requireNonNull(field);
		int diff = intervalDirection();
		return diff == 0 || (diff > 0 
				? getStart().compareTo(field) <= 0 && getExclusifEnd().compareTo(field) > 0
				: getStart().compareTo(field) <= 0 || getExclusifEnd().compareTo(field) > 0);
	}

	default boolean containsInterval(Interval<T> p) {
		requireNonNull(p);
		return containsInterval(p.getStart(), p.getExclusifEnd(), p::intervalDirection);
	}
	
	default boolean containsInterval(T start, T exclusifEnd) {
		return containsInterval(requireNonNull(start), requireNonNull(exclusifEnd), ()-> direction(start, exclusifEnd));
	}

	private boolean containsInterval(T start, T exclusifEnd, IntSupplier dirSupp) {
		int diff = intervalDirection();
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
		requireNonNull(p);
		return intersectInterval(p.getStart(), p.getExclusifEnd(), p::intervalDirection);
	}
	
	default boolean intersectInterval(T start, T exclusifEnd) {
		return intersectInterval(requireNonNull(start), requireNonNull(exclusifEnd), ()-> direction(start, exclusifEnd));
	}

	private boolean intersectInterval(T start, T exclusifEnd, IntSupplier dirSupp) {
		int diff = intervalDirection();
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
		return intervalDirection() <= 0;
	}
	
	default boolean isRegular() {
		return intervalDirection() > 0;
	}

	default boolean symmetrical(Interval<T> p) {
		requireNonNull(p);
		return this.getStart().equals(p.getExclusifEnd()) 
			&& this.getExclusifEnd().equals(p.getStart());
	}
	
	default int intervalDirection() {
		return direction(getStart(), getExclusifEnd());
	}
}
