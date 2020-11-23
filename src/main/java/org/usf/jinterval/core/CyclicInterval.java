package org.usf.jinterval.core;

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
		int diff = getExclusifEnd().compareTo(getStart());
		if(diff == 0) {
			return true;
		}
		int pDiff = exclusifEnd.compareTo(start);
		if(pDiff == 0 || pDiff < 0 && diff > 0) {
			return false;
		}
		return diff != pDiff 
				? getStart().compareTo(start)>=0 || getExclusifEnd().compareTo(exclusifEnd)<=0
				:!(getStart().compareTo(start)>0 || getExclusifEnd().compareTo(exclusifEnd)<0);
				
	}

	@Override
	default boolean intersectInterval(T start, T exclusifEnd) {
		int diff = getExclusifEnd().compareTo(getStart());
		if(diff == 0) {
			return true;
		}
		int pDiff = exclusifEnd.compareTo(start);
		if(pDiff == 0 || pDiff < 0 && diff < 0) {
			return true;
		}
		return !(diff == pDiff
				? getStart().compareTo(exclusifEnd)>=0 || getExclusifEnd().compareTo(start)<=0
				: getStart().compareTo(exclusifEnd)>=0 && getExclusifEnd().compareTo(start)<=0);
	}
	
	@Override
	default Optional<Interval<T>> intervalIntersection(T start, T exclusifEnd) {

		throw new UnsupportedOperationException();
	}
}
