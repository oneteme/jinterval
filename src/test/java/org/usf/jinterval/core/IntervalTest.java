package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class IntervalTest extends RegularIntervalTest {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsField(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);

		testContainsField(in, in.getStart(), true);
		testContainsField(in, in.shiftStart(1), true);
		testContainsField(in, in.shiftStart(-1), false);
		
		testContainsField(in, in.getExclusifEnd(), false);
		testContainsField(in, in.shiftExclusifEnd(1), false);
		testContainsField(in, in.shiftExclusifEnd(-1), true);
		
		assertTrue(in.shiftStart(0, 0).containsField(in.getStart()));
		assertTrue(in.shiftExclusifEnd(0, 0).containsField(in.getExclusifEnd()));
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		
		testContainsInterval(in, in, true, false); //in
		
		testContainsInterval(in, in.shift(1, 0), true, false);
		testContainsInterval(in, in.shift(0, -1), true, false);
		testContainsInterval(in, in.shift(1, -1), true, false);
		
		testContainsInterval(in, in.shift(-1, 0), false,  false); //out
		testContainsInterval(in, in.shift(0, 1), false,  false);
		testContainsInterval(in, in.shift(-1, 1), false,  false);
		
		testContainsInterval(in, in.shiftStart(-1, 0), false,  true); //out start
		testContainsInterval(in, in.shiftStart(0, 1), true,  false);
		testContainsInterval(in, in.shiftStart(-1, 1), false,  false);
		
		testContainsInterval(in, in.shiftExclusifEnd(-1, 0), true,  false); //out end
		testContainsInterval(in, in.shiftExclusifEnd(0, 1), false,  true);
		testContainsInterval(in, in.shiftExclusifEnd(-1, 1), false,  false);
		
		testContainsInterval(in, in.shiftStart(0, 0), false, false);
		testContainsInterval(in, in.shiftExclusifEnd(0, 0), false,  false);
		testContainsInterval(in.shiftStart(0, 0), in, true, true);
		testContainsInterval(in.shiftExclusifEnd(0, 0), in, true,  true);
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIntersectInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		
		testIntersectInterval(in, in, true, false); //in
		
		testIntersectInterval(in, in.shift(1, 0), true, false);
		testIntersectInterval(in, in.shift(0, -1), true, false);
		testIntersectInterval(in, in.shift(1, -1), true, false);
		
		testIntersectInterval(in, in.shift(-1, 0), true,  true); //out
		testIntersectInterval(in, in.shift(0, 1), true,  true);
		testIntersectInterval(in, in.shift(-1, 1), true,  true);
		
		testIntersectInterval(in, in.shiftStart(-1, 0), false,  true); //out start
		testIntersectInterval(in, in.shiftStart(0, 1), true,  false);
		testIntersectInterval(in, in.shiftStart(-1, 1), true,  true);
		
		testIntersectInterval(in, in.shiftExclusifEnd(-1, 0), true,  false); //out end
		testIntersectInterval(in, in.shiftExclusifEnd(0, 1), false,  true);
		testIntersectInterval(in, in.shiftExclusifEnd(-1, 1), true,  true);
		
		testIntersectInterval(in, in.shiftStart(0, 0), true, true);
		testIntersectInterval(in, in.shiftExclusifEnd(0, 0), true,  true);
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIsRegular(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {

		super.testIsRegular(start, exclusifEnd, getFn);
		var in = ofInterval(start, exclusifEnd, getFn);
		assertFalse(in.reverseInterval().isRegular());
		assertFalse(in.shiftStart(0, 0).isRegular());
		assertFalse(in.shiftExclusifEnd(0, 0).isRegular());
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIsInverted(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {

		super.testIsInverted(start, exclusifEnd, getFn);
		var in = ofInterval(start, exclusifEnd, getFn);
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
	
	<T extends Comparable<? super T>> void testContainsField(IntervalShiftingProxy<T> ip, T field, boolean expected) {
		
		assertEquals(expected, ip.containsField(field));
		assertEquals(!expected, ip.reverseInterval().containsField(field));
	}

	<T extends Comparable<? super T>> void testContainsInterval(IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval, boolean expected, boolean expectedR) {
		
		assertEquals(expected, ip.containsInterval(interval));
		assertEquals(expectedR, ip.reverseInterval().containsInterval(interval));
		assertEquals(expected && expectedR, ip.containsInterval(interval.reverseInterval()));
		
		assertEquals(expected, ip.containsInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expectedR, ip.reverseInterval().containsInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expected && expectedR, ip.containsInterval(interval.reverseInterval().getStart(), interval.reverseInterval().getExclusifEnd()));
	}
	
	<T extends Comparable<? super T>> void testIntersectInterval(IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval, boolean expected, boolean expextedR) {
		
		assertEquals(expected, ip.intersectInterval(interval));
		assertEquals(expected, interval.intersectInterval(ip));
		
		assertEquals(expextedR, ip.reverseInterval().intersectInterval(interval));
		assertEquals(expextedR, interval.intersectInterval(ip.reverseInterval()));

		assertEquals(true, ip.reverseInterval().intersectInterval(interval.reverseInterval()));
		assertEquals(true, interval.reverseInterval().intersectInterval(ip.reverseInterval()));
		

		assertEquals(expected, ip.intersectInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expected, interval.intersectInterval(ip.getStart(), ip.getExclusifEnd()));
		
		assertEquals(expextedR, ip.reverseInterval().intersectInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expextedR, interval.intersectInterval(ip.reverseInterval().getStart(), ip.reverseInterval().getExclusifEnd()));

		assertEquals(true, ip.reverseInterval().intersectInterval(interval.reverseInterval().getStart(), interval.reverseInterval().getExclusifEnd()));
		assertEquals(true, interval.reverseInterval().intersectInterval(ip.reverseInterval().getStart(), ip.reverseInterval().getExclusifEnd()));
	}
	
	static Stream<Arguments> temporalIntervals() { //override period interval
		 return Stream.of(
    		Arguments.arguments(LocalTime.of(0 , 40), LocalTime.of(0, 55), shiftLocalTime)
	    );
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
