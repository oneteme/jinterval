package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.jinterval.Utils.assertExceptionMsg;

import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RegularIntervalTest implements CommonTestInterval {

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
		assertExceptionMsg(UnsupportedOperationException.class, ()-> in.reverseInterval(), "not supported in a regular interval");
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testSymmetrical(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		assertExceptionMsg(UnsupportedOperationException.class, ()-> in.symmetrical(in), "not supported in a regular interval");
	}
	
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testRequiredRegularInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		assertDoesNotThrow(()-> RegularInterval.requiredRegularInterval(start, exclusifEnd));
		assertExceptionMsg(IllegalArgumentException.class, ()-> RegularInterval.requiredRegularInterval(exclusifEnd, start), "regular interval is required");
	}
	
	@Override
	public <T extends Comparable<? super T>> void testContainsField(IntervalShiftingProxy<T> ip, T field, boolean expected, boolean expectedR) {

		if(isRegular(ip)) {
			assertEquals(expected, ip.containsField(field));
		}
	}
	
	@Override
	public <T extends Comparable<? super T>> void testContainsInterval(IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval, boolean expected, boolean expectedR) {

		if(isRegular(ip) && isRegular(interval)) {
			assertEquals(expected, ip.containsInterval(interval));
			assertEquals(expected, ip.containsInterval(interval.getStart(), interval.getExclusifEnd()));
		}
	}
	
	@Override
	public <T extends Comparable<? super T>> void testIntersectInterval(IntervalShiftingProxy<T> ip,
			IntervalShiftingProxy<T> interval, boolean expected, boolean expextedR) {

		if(isRegular(ip) && isRegular(interval)) {
			assertEquals(expected, ip.intersectInterval(interval));
			assertEquals(expected, ip.intersectInterval(interval.getStart(), interval.getExclusifEnd()));
		}
	}
	
	private static <T extends Comparable<? super T>> boolean isRegular(Interval<T> in) {
		return in.getExclusifEnd().compareTo(in.getStart()) > 0;
	}


	@Override
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
