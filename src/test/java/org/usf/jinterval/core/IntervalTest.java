package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class IntervalTest extends CommonTestInterval {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIsRegular(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {

		var in = ofInterval(start, exclusifEnd, getFn);
		assertTrue(in.isRegular());
		assertFalse(in.reverseInterval().isRegular());
		assertFalse(in.shiftStart(0, 0).isRegular());
		assertFalse(in.shiftExclusifEnd(0, 0).isRegular());
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIsInverted(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {

		var in = ofInterval(start, exclusifEnd, getFn);
		assertFalse(in.isInverted());
		assertTrue(in.reverseInterval().isInverted());
		assertTrue(in.shiftStart(0, 0).isInverted());
		assertTrue(in.shiftExclusifEnd(0, 0).isInverted());
	}

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testReverseInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		var in = ofInterval(start, exclusifEnd, getFn);
		var rev = in.reverseInterval(this::create);
		assertEquals(in.getStart(), rev.getExclusifEnd());
		assertEquals(in.getExclusifEnd(), rev.getStart());
	}

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testSymmetrical(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		var in = ofInterval(start, exclusifEnd, getFn);
		var rev = in.reverseInterval();
		
		assertTrue(in.symmetrical(rev));
		assertTrue(rev.symmetrical(in));
		
		assertFalse(in.symmetrical(in));
		assertFalse(in.symmetrical(rev.shift(1, 0)));
		assertFalse(in.symmetrical(rev.shift(0, 1)));

		assertFalse(rev.symmetrical(rev));
		assertFalse(rev.symmetrical(in.shift(1, 0)));
		assertFalse(rev.symmetrical(in.shift(0, 1)));
	}
	
	@Override
	<T extends Comparable<? super T>> void testContainsField(boolean expected, IntervalShiftingProxy<T> ip, T field) {
		
		assertEquals(expected, ip.containsField(field));
	}

	@Override
	<T extends Comparable<? super T>> void testContainsInterval(boolean expected, IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval) {

		assertEquals(expected, ip.containsInterval(interval));
		assertEquals(expected, ip.containsInterval(interval.getStart(), interval.getExclusifEnd()));
	}

	@Override
	<T extends Comparable<? super T>> void testIntersectInterval(boolean expected, IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval) {
		
		assertEquals(expected, ip.intersectInterval(interval));
		assertEquals(expected, ip.intersectInterval(interval.getStart(), interval.getExclusifEnd()));
	}
	
	@Override
	public <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd){
		
		return new Interval<T>() {
			@Override
			public T getExclusifEnd() {
				return exclusifEnd;
			}
			@Override
			public T getStart() {
				return start;
			}
		};
	}

}
