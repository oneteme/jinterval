package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.jinterval.Utils.assertExceptionMsg;

import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RegularIntervalTest implements IntervalFactory {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsField(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		
		assertTrue(in.containsField(in.getStart()));
		assertTrue(in.containsField(in.shiftStart(1)));
		assertFalse(in.containsField(in.shiftStart(-1)));
		
		assertFalse(in.containsField(in.getExclusifEnd()));
		assertFalse(in.containsField(in.shiftExclusifEnd(1)));
		assertTrue(in.containsField(in.shiftExclusifEnd(-1)));
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		
		assertTrue(in.containsInterval(in));

		assertTrue(in.containsInterval(in.shift(1, 0)));
		assertTrue(in.containsInterval(in.shift(0, -1)));
		assertTrue(in.containsInterval(in.shift(1, -1)));

		assertFalse(in.containsInterval(in.shift(-1, 0)));
		assertFalse(in.containsInterval(in.shift(0, 1)));
		assertFalse(in.containsInterval(in.shift(-1, 1)));

		assertFalse(in.containsInterval(in.shiftStart(-1, 0)));
		assertTrue(in.containsInterval(in.shiftStart(0, 1)));
		assertFalse(in.containsInterval(in.shiftStart(-1, 1)));
		
		assertTrue(in.containsInterval(in.shiftExclusifEnd(-1, 0)));
		assertFalse(in.containsInterval(in.shiftExclusifEnd(0, 1)));
		assertFalse(in.containsInterval(in.shiftExclusifEnd(-1, 1)));
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIntersectInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		
		assertTrue(in.intersectInterval(in));

		assertTrue(in.intersectInterval(in.shift(1, 0)));
		assertTrue(in.intersectInterval(in.shift(0, -1)));
		assertTrue(in.intersectInterval(in.shift(1, -1)));

		assertTrue(in.intersectInterval(in.shift(-1, 0)));
		assertTrue(in.intersectInterval(in.shift(0, 1)));
		assertTrue(in.intersectInterval(in.shift(-1, 1)));

		assertFalse(in.intersectInterval(in.shiftStart(-1, 0)));
		assertTrue(in.intersectInterval(in.shiftStart(0, 1)));
		assertTrue(in.intersectInterval(in.shiftStart(-1, 1)));
		
		assertTrue(in.intersectInterval(in.shiftExclusifEnd(-1, 0)));
		assertFalse(in.intersectInterval(in.shiftExclusifEnd(0, 1)));
		assertTrue(in.intersectInterval(in.shiftExclusifEnd(-1, 1)));
	}

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIsRegular(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		assertTrue(in.isRegular());
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIsInverted(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		assertFalse(in.isInverted());
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testReverseInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		assertExceptionMsg(UnsupportedOperationException.class, ()-> in.reverseInterval(), "cannot reverse regular interval");
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testSymmetrical(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		assertExceptionMsg(UnsupportedOperationException.class, ()-> in.symmetrical(in), "cannot reverse regular interval");
	}

	public <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd){
		
		return new RegularInterval<T>() {
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
