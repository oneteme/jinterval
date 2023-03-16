package org.usf.java.jinterval.core;

import static java.time.Duration.between;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public interface TemporalInterval<T extends Temporal & Comparable<? super T>> extends Interval<T> {
	
	default Duration duration() {
		return between(startInclusive(), endExclusive());
	}

    default long duration(ChronoUnit unit) {
        return unit.between(startInclusive(), endExclusive());
    }
    
    default long unitCount(ChronoUnit unit, int step) {
    	var diff = duration(unit);
		if(diff % step == 0) {
			return diff / step;
		}
        throw new IllegalArgumentException("duration % step != 0");
    }
}
