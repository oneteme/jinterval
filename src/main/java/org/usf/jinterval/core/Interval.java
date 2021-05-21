package org.usf.jinterval.core;

import static java.util.Objects.requireNonNull;
import static org.usf.jinterval.core.IntervalUtils.direction;

import java.util.function.IntSupplier;

public interface Interval<T extends Comparable<? super T>> {
	
	T startInclusive();

	T endExclusive();
	
	default boolean containsField(T field) {
		requireNonNull(field);
		int diff = intervalDirection();
		return diff == 0 || (diff > 0 
				? startInclusive().compareTo(field) <= 0 && endExclusive().compareTo(field) > 0
				: startInclusive().compareTo(field) <= 0 || endExclusive().compareTo(field) > 0);
	}

	default boolean containsInterval(Interval<T> p) {
		requireNonNull(p);
		return containsInterval(p.startInclusive(), p.endExclusive(), p::intervalDirection);
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
				? startInclusive().compareTo(start) <= 0 && endExclusive().compareTo(exclusifEnd) >= 0
				: startInclusive().compareTo(start) <= 0 || endExclusive().compareTo(exclusifEnd) >= 0;
	}
	
	default boolean intersectInterval(Interval<T> p) {
		requireNonNull(p);
		return intersectInterval(p.startInclusive(), p.endExclusive(), p::intervalDirection);
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
				? startInclusive().compareTo(exclusifEnd) < 0 && endExclusive().compareTo(start) > 0
				: startInclusive().compareTo(exclusifEnd) < 0 || endExclusive().compareTo(start) > 0;
	}
	
	default boolean inInterval(Interval<T> p) {
		return requireNonNull(p).containsInterval(this);
	}

	default boolean notInInterval(Interval<T> p) {
		return !requireNonNull(p).containsInterval(this);
	}
	
	default boolean isInverted() {
		return intervalDirection() <= 0;
	}
	
	default boolean isRegular() {
		return intervalDirection() > 0;
	}

	default boolean symmetrical(Interval<T> p) {
		requireNonNull(p);
		return this.startInclusive().equals(p.endExclusive()) 
			&& this.endExclusive().equals(p.startInclusive());
	}
	
	default int intervalDirection() {
		return direction(startInclusive(), endExclusive());
	}
}
