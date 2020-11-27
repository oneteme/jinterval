package org.usf.jinterval.core;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.valueOf;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.Month.MARCH;
import static java.time.Month.SEPTEMBER;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class IntervalTest {

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsField(IntervalShiftingProxy<T> ip) {

		testContainsField(ip, ip.getStart(), true);
		testContainsField(ip, ip.shiftStart(1), true);
		testContainsField(ip, ip.shiftStart(-1), false);
		
		testContainsField(ip, ip.getExclusifEnd(), false);
		testContainsField(ip, ip.shiftExclusifEnd(1), false);
		testContainsField(ip, ip.shiftExclusifEnd(-1), true);
		
		assertTrue(ip.shiftStart(0, 0).containsField(ip.getStart()));
		assertTrue(ip.shiftExclusifEnd(0, 0).containsField(ip.getExclusifEnd()));
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsInterval(IntervalShiftingProxy<T> ip) {
		
		testContainsInterval(ip, ip, true, false); //in
		
		testContainsInterval(ip, ip.shift(1, 0), true, false);
		testContainsInterval(ip, ip.shift(0, -1), true, false);
		testContainsInterval(ip, ip.shift(1, -1), true, false);
		
		testContainsInterval(ip, ip.shift(-1, 0), false,  false); //out
		testContainsInterval(ip, ip.shift(0, 1), false,  false);
		testContainsInterval(ip, ip.shift(-1, 1), false,  false);
		
		testContainsInterval(ip, ip.shiftStart(-1, 0), false,  true); //out start
		testContainsInterval(ip, ip.shiftStart(0, 1), true,  false);
		testContainsInterval(ip, ip.shiftStart(-1, 1), false,  false);
		
		testContainsInterval(ip, ip.shiftExclusifEnd(-1, 0), true,  false); //out end
		testContainsInterval(ip, ip.shiftExclusifEnd(0, 1), false,  true);
		testContainsInterval(ip, ip.shiftExclusifEnd(-1, 1), false,  false);
		
		testContainsInterval(ip, ip.shiftStart(0, 0), false, false);
		testContainsInterval(ip, ip.shiftExclusifEnd(0, 0), false,  false);
		testContainsInterval(ip.shiftStart(0, 0), ip, true, true);
		testContainsInterval(ip.shiftExclusifEnd(0, 0), ip, true,  true);
	}
	
	
	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIntersectInterval(IntervalShiftingProxy<T> ip) {
		
		testIntersectInterval(ip, ip, true, false); //in
		
		testIntersectInterval(ip, ip.shift(1, 0), true, false);
		testIntersectInterval(ip, ip.shift(0, -1), true, false);
		testIntersectInterval(ip, ip.shift(1, -1), true, false);
		
		testIntersectInterval(ip, ip.shift(-1, 0), true,  true); //out
		testIntersectInterval(ip, ip.shift(0, 1), true,  true);
		testIntersectInterval(ip, ip.shift(-1, 1), true,  true);
		
		testIntersectInterval(ip, ip.shiftStart(-1, 0), false,  true); //out start
		testIntersectInterval(ip, ip.shiftStart(0, 1), true,  false);
		testIntersectInterval(ip, ip.shiftStart(-1, 1), true,  true);
		
		testIntersectInterval(ip, ip.shiftExclusifEnd(-1, 0), true,  false); //out end
		testIntersectInterval(ip, ip.shiftExclusifEnd(0, 1), false,  true);
		testIntersectInterval(ip, ip.shiftExclusifEnd(-1, 1), true,  true);
		
		testIntersectInterval(ip, ip.shiftStart(0, 0), true, true);
		testIntersectInterval(ip, ip.shiftExclusifEnd(0, 0), true,  true);
	}
	
	<T extends Comparable<? super T>> void testContainsField(IntervalShiftingProxy<T> ip, T field, boolean expected) {
		
		assertEquals(expected, ip.containsField(field));
		assertEquals(!expected, ip.revert().containsField(field));
	}

	<T extends Comparable<? super T>> void testContainsInterval(IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval, boolean expected, boolean expectedR) {
		
		assertEquals(expected, ip.containsInterval(interval));
		assertEquals(expectedR, ip.revert().containsInterval(interval));
		assertEquals(expected && expectedR, ip.containsInterval(interval.revert()));
		
		assertEquals(expected, ip.containsInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expectedR, ip.revert().containsInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expected && expectedR, ip.containsInterval(interval.revert().getStart(), interval.revert().getExclusifEnd()));
	}
	
	<T extends Comparable<? super T>> void testIntersectInterval(IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval, boolean expected, boolean expextedR) {
		
		assertEquals(expected, ip.intersectInterval(interval));
		assertEquals(expected, interval.intersectInterval(ip));
		
		assertEquals(expextedR, ip.revert().intersectInterval(interval));
		assertEquals(expextedR, interval.intersectInterval(ip.revert()));

		assertEquals(true, ip.revert().intersectInterval(interval.revert()));
		assertEquals(true, interval.revert().intersectInterval(ip.revert()));
		

		assertEquals(expected, ip.intersectInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expected, interval.intersectInterval(ip.getStart(), ip.getExclusifEnd()));
		
		assertEquals(expextedR, ip.revert().intersectInterval(interval.getStart(), interval.getExclusifEnd()));
		assertEquals(expextedR, interval.intersectInterval(ip.revert().getStart(), ip.revert().getExclusifEnd()));

		assertEquals(true, ip.revert().intersectInterval(interval.revert().getStart(), interval.revert().getExclusifEnd()));
		assertEquals(true, interval.revert().intersectInterval(ip.revert().getStart(), ip.revert().getExclusifEnd()));
	}
	

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals"})
	<T extends Comparable<? super T>> void testIsInverted(IntervalShiftingProxy<T> interval) {//increase test cov
	
		assertFalse(interval.isInverted());
		assertTrue(interval.revert().isInverted());
	}

	static Stream<Arguments> numberIntervals() {
	    return Stream.of(
    		Arguments.arguments(ofInterval(1, 5, (a,b)-> a+b)),
    		Arguments.arguments(ofInterval(-5L, 1L, (a,b)-> a+b)),
    		Arguments.arguments(ofInterval(-30., -20., (a,b)-> a+b)),
    		Arguments.arguments(ofInterval(ONE, TEN, (a,b)-> a.add(valueOf(b))))
	    );
	}
	
	static Stream<Arguments> enumIntervals() { 
		 return Stream.of(
    		Arguments.arguments(ofInterval(MARCH, SEPTEMBER, (a,b)-> Month.values()[a.ordinal() + b])),
    		Arguments.arguments(ofInterval(TUESDAY, FRIDAY, (a,b)-> DayOfWeek.values()[a.ordinal() + b]))
	    );
	}

	static Stream<Arguments> periodIntervals() {
		 return Stream.of(
    		Arguments.arguments(ofPeriod(LocalTime.of(0 , 40), LocalTime.of(0, 55), MINUTES))
	    );
	}

	@SuppressWarnings("unchecked")
	static <T extends Comparable<? super T> & Temporal> IntervalShiftingProxy<T> ofPeriod(T start, T exclusifEnd, ChronoUnit temporalUnit){
		
		return ofInterval(start, exclusifEnd, (o,v)->(T) o.plus(v, temporalUnit));
	}
	
	static <T extends Comparable<? super T>> IntervalShiftingProxy<T> ofInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){
		
		return new IntervalShiftingProxy<>(create(start, exclusifEnd), IntervalTest::create, getFn);
	}
	
	static <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd){
		
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
