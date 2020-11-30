package org.usf.jinterval.core;

import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

abstract class CommonTestInterval implements IntervalFactory {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	final <T extends Comparable<? super T>> void testContainsField(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);

		testContainsField(in, in.getStart(), true, false);
		testContainsField(in, in.shiftStart(1), true, false);
		testContainsField(in, in.shiftStart(-1), false, true);
		
		testContainsField(in, in.getExclusifEnd(), false, true);
		testContainsField(in, in.shiftExclusifEnd(1), false, true);
		testContainsField(in, in.shiftExclusifEnd(-1), true, false);
		
		testContainsField(in.shiftStart(0, 0), in.getStart(), true, true);
		testContainsField(in.shiftExclusifEnd(0, 0), in.getExclusifEnd(), true, true);
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	final <T extends Comparable<? super T>> void testContainsInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
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
	final <T extends Comparable<? super T>> void testIntersectInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
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
	
	<T extends Comparable<? super T>> void testContainsField(IntervalShiftingProxy<T> ip, T field, boolean expected, boolean expectedR) {
		
		testContainsField(expected, ip, field);
		testContainsField(expectedR, ip.reverseInterval(), field);
	}
	
	<T extends Comparable<? super T>> void testContainsInterval(IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval, boolean expected, boolean expectedR) {
		
		testContainsInterval(expected, ip, interval);
		testContainsInterval(expectedR, ip.reverseInterval(), interval);
		testContainsInterval(expected && expectedR, ip, interval.reverseInterval());
	}
	
	<T extends Comparable<? super T>> void testIntersectInterval(IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval, boolean expected, boolean expextedR) {
		
		testIntersectInterval(expected, ip, interval);
		testIntersectInterval(expected, interval, ip);
		
		testIntersectInterval(expextedR, ip.reverseInterval(), interval);
		testIntersectInterval(expextedR, interval, ip.reverseInterval());

		testIntersectInterval(true, ip.reverseInterval(), interval.reverseInterval());
		testIntersectInterval(true, interval.reverseInterval(), ip.reverseInterval());
	}
	
	<T extends Comparable<? super T>> void testContainsField(boolean expected, IntervalShiftingProxy<T> ip, T field) {
		throw new RuntimeException();
	}
	
	<T extends Comparable<? super T>> void testContainsInterval(boolean expected, IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval){
		throw new RuntimeException();
	}
	
	<T extends Comparable<? super T>> void testIntersectInterval(boolean expected, IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval){
		throw new RuntimeException();
	}
}
