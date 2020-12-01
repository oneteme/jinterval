package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

final class IntervalsTest implements CommonTestInterval2 {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void  testMax(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		assertEquals(exclusifEnd, Intervals.max(start, exclusifEnd));
		assertEquals(exclusifEnd, Intervals.max(exclusifEnd, start));
		assertNotEquals(start, Intervals.max(start, exclusifEnd));
		assertNotEquals(start, Intervals.max(exclusifEnd, start));
	}

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void  testMin(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		assertEquals(start, Intervals.min(start, exclusifEnd));
		assertEquals(start, Intervals.min(exclusifEnd, start));
		assertNotEquals(exclusifEnd, Intervals.min(start, exclusifEnd));
		assertNotEquals(exclusifEnd, Intervals.min(exclusifEnd, start));
	}
	
	@Override
	public <T extends Comparable<? super T>> void testEquals(boolean expected, Interval<T> ip, Interval<T> interval) {

		assertEquals(expected, Intervals.equals(ip, interval));
		assertEquals(expected, Intervals.equals(interval, ip));
	}

	@Override
	public <T extends Comparable<? super T>> void testToString(String expected, Interval<T> ip) {

		assertEquals(expected, Intervals.toString(ip));
		assertEquals(expected, Intervals.toString(ip.getStart(), ip.getExclusifEnd()));
	}

	@Override
	public <T extends Comparable<? super T>> ImmutableInterval<T> create(T start, T exclusifEnd) {
		return ImmutableInterval.of(start, exclusifEnd);
	}

}
