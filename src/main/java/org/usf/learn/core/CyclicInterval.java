package org.usf.learn.core;

import java.util.Optional;

public interface CyclicInterval<T extends Comparable<? super T>> extends Interval<T> {
	
	default boolean isInverted() {
		return getStart().compareTo(getExclusifEnd()) >= 0;
	}
	
	@Override
	default boolean containsField(T temporal) {
		return isInverted() ^ 
				(getStart().compareTo(temporal) <= 0 && getExclusifEnd().compareTo(temporal) > 0);
	}

	@Override
	default boolean containsInterval(T start, T exclusifEnd) {
		int diff = start.compareTo(exclusifEnd);
		if(diff == 0) {
			return true;
		}
		boolean pInverted = start.compareTo(exclusifEnd) >= 0;
		boolean isInverted = diff > 0;
		if(!isInverted && pInverted) {
			return false;
		}
		if(getStart().compareTo(start)<=0) {
			return isInverted != pInverted || getExclusifEnd().compareTo(exclusifEnd)>=0;
		}
		return false;
	}

	@Override
	default boolean intersectInterval(T start, T exclusifEnd) {
		int diff = start.compareTo(exclusifEnd);
		if(diff == 0) {
			return true;
		}
		boolean pInverted = start.compareTo(exclusifEnd) >= 0;
		boolean isInverted = diff > 0;
		if(isInverted && pInverted) {
			return true;
		}
		if(getStart().compareTo(exclusifEnd)<0) {
			return isInverted != pInverted || getExclusifEnd().compareTo(start)>0;
		}
		return false;
	}
	
	@Override
	default Optional<Interval<T>> intervalIntersection(T start, T exclusifEnd) {
		// TODO Auto-generated method stub
		return null;
	}
}
