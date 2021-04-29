package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class IntervalsTest {
	
	@Test
	void testIsMissingIntervals() {
		
		var v1 = Arrays.asList(interval(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)));
		assertFalse(Intervals.isMissingIntervals(v1));
		

		var v2 = Arrays.asList(interval(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 10)), 
				interval(LocalDate.of(2020, 1, 5), LocalDate.of(2020, 1, 20)));
		assertFalse(Intervals.isMissingIntervals(v2));
		

		var v3 = Arrays.asList(interval(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 10)), 
				interval(LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 20)));
		assertTrue(Intervals.isMissingIntervals(v3));
		
	}
	
	private static <T extends Comparable<? super T>> Interval<T> interval(T start, T exludedEnd) {
		
		return new ImmutableInterval<T>(start, exludedEnd);
	}

}
