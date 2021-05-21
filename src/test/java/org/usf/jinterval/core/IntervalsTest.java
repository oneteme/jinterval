package org.usf.jinterval.core;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.usf.jinterval.Utils.assertExceptionMsg;
import static org.usf.jinterval.Utils.intervals;
import static org.usf.jinterval.core.Intervals.*;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.usf.jinterval.core.exception.IntervalMismatchException;

class IntervalsTest {
	
	@Test
	void testMissingIntervals() {
		
		testMissingIntervals(false, emptyList(), emptyList());
		testMissingIntervals(false, intervals(-1,3, 5,7, 10,15), intervals(3,5, 7,10));
		testMissingIntervals(true, intervals(-1,3, 5,7, 4,15), intervals(3,4, 15,-1));
		testMissingIntervals(true, intervals(-1,3, 5,7, 4,15, 3,5, 15,1), emptyList());
	}
	
	@Test
	void testOverlapingIntervals() {

		testOverlapingIntervals(false, emptyList(), emptyList());
		testOverlapingIntervals(false, intervals(-1,3, 5,7, 10,15), emptyList());
		testOverlapingIntervals(true, intervals(-1,3, 5,7, 4,15), intervals(5,7));
		testOverlapingIntervals(true, intervals(-1,3, 5,7, 4,15, 3,5, 15,1), intervals(-1,1, 4,5, 5,7));
	}
	
	@Test
	void testMissingAngOverlapingIntervals() {

		testMissingAngOverlapingIntervals(false, emptyList(), emptyList());
		testMissingAngOverlapingIntervals(false, intervals(-1,3, 5,7, 10,15), intervals(3,5, 7,10));
		testMissingAngOverlapingIntervals(true, intervals(-1,3, 5,7, 4,15), intervals(3,4, 5,7, 15,-1));
		testMissingAngOverlapingIntervals(true, intervals(-1,3, 5,7, 4,15, 3,5, 15,1), intervals(-1,1, 4,5, 5,7));
	}
	
	private void testMissingIntervals(boolean inverted, Collection<? extends Interval<Integer>> intervals, Collection<? extends Interval<Integer>> expected) {
		
		assertNotEquals(expected.isEmpty(), isMissingIntervals(inverted, intervals));
		assertArrayEquals(expected.toArray(), missingIntervals(inverted, intervals).toArray());
		if(expected.isEmpty()) {			
			assertDoesNotThrow(()-> requiredNotMissingIntervals(inverted, intervals));
		}
		else {
			assertExceptionMsg(IntervalMismatchException.class, ()-> requiredNotMissingIntervals(inverted, intervals), "missing interval");
		}
		assertArrayEquals(expected.toArray(), collectMissingIntervals(inverted, intervals, IntervalsTest::newInterval, Collectors.toList()).toArray());
	}
	
	private void testOverlapingIntervals(boolean inverted, Collection<? extends Interval<Integer>> intervals, Collection<? extends Interval<Integer>> expected) {
		
		assertNotEquals(expected.isEmpty(), isOverlappingIntervals(inverted, intervals));
		assertArrayEquals(expected.toArray(), overlappingIntervals(inverted, intervals).toArray());
		if(expected.isEmpty()) {
			assertDoesNotThrow(()-> requiredNotOverlappingIntervals(inverted, intervals));
		}
		else {
			assertExceptionMsg(IntervalMismatchException.class, ()-> requiredNotOverlappingIntervals(inverted, intervals), "overlaping interval");
		}
		assertArrayEquals(expected.toArray(), collectOverlappingIntervals(inverted, intervals, IntervalsTest::newInterval, Collectors.toList()).toArray());
	}
	
	private void testMissingAngOverlapingIntervals(boolean inverted, Collection<? extends Interval<Integer>> intervals, Collection<? extends Interval<Integer>> expected) {
		
		assertNotEquals(expected.isEmpty(), isMissingOrOverlappingIntervals(inverted, intervals));
		assertArrayEquals(expected.toArray(), missingAndOverlappingIntervals(inverted, intervals).toArray());
		if(expected.isEmpty()) {			
			assertDoesNotThrow(()-> requiredNotMissingAndOverlappingIntervals(inverted, intervals));
		}
		else {
			assertExceptionMsg(IntervalMismatchException.class, ()-> requiredNotMissingAndOverlappingIntervals(inverted, intervals), "mismatch interval");
		}
		assertArrayEquals(expected.toArray(), collectMissingAndOverlappingIntervals(inverted, intervals, IntervalsTest::newInterval, Collectors.toList()).toArray());
	}
	
	
	
	private static <T extends Comparable<? super T>> Interval<T> newInterval(T s, T e) {
	
		return new Interval<T>() {
			@Override
			public T startInclusive() {return s;}
			@Override
			public T endExclusive() {return e;}
		};
	}
	
}
