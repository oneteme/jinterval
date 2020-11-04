package org.usf.learn.core;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public interface Period<T extends Temporal & Comparable<? super T>> extends Interval<T> {

	ChronoUnit getTemporalUnit();
	
	default long between() {
		return getStart().until(getExclusifEnd(), getTemporalUnit());
	}
}
