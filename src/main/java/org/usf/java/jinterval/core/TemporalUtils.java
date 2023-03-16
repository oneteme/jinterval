package org.usf.java.jinterval.core;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TemporalUtils {

	public static long nStepBetween(Temporal startInclusive, Temporal exclusifEnd, int step, ChronoUnit unit) {
    	var diff = unit.between(startInclusive, exclusifEnd);
		if(diff % step == 0) {
			return diff / step;
		}
        throw new IllegalArgumentException("duration % step != 0");
	}

}
