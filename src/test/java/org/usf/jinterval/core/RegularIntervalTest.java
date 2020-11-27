package org.usf.jinterval.core;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.valueOf;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.Month.MARCH;
import static java.time.Month.SEPTEMBER;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RegularIntervalTest {

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsField(IntervalShiftingProxy<T> ip) {

		assertTrue(ip.containsField(ip.getStart()));
		assertTrue(ip.containsField(ip.shiftStart(1)));
		assertFalse(ip.containsField(ip.shiftStart(-1)));
		
		assertFalse(ip.containsField(ip.getExclusifEnd()));
		assertFalse(ip.containsField(ip.shiftExclusifEnd(1)));
		assertTrue(ip.containsField(ip.shiftExclusifEnd(-1)));
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testContainsInterval(IntervalShiftingProxy<T> ip) {

		assertTrue(ip.containsInterval(ip));

		assertTrue(ip.containsInterval(ip.shift(1, 0)));
		assertTrue(ip.containsInterval(ip.shift(0, -1)));
		assertTrue(ip.containsInterval(ip.shift(1, -1)));

		assertFalse(ip.containsInterval(ip.shift(-1, 0)));
		assertFalse(ip.containsInterval(ip.shift(0, 1)));
		assertFalse(ip.containsInterval(ip.shift(-1, 1)));

		assertFalse(ip.containsInterval(ip.shiftStart(-1, 0)));
		assertTrue(ip.containsInterval(ip.shiftStart(0, 1)));
		assertFalse(ip.containsInterval(ip.shiftStart(-1, 1)));
		
		assertTrue(ip.containsInterval(ip.shiftExclusifEnd(-1, 0)));
		assertFalse(ip.containsInterval(ip.shiftExclusifEnd(0, 1)));
		assertFalse(ip.containsInterval(ip.shiftExclusifEnd(-1, 1)));
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testIntersectInterval(IntervalShiftingProxy<T> ip) {

		assertTrue(ip.intersectInterval(ip));

		assertTrue(ip.intersectInterval(ip.shift(1, 0)));
		assertTrue(ip.intersectInterval(ip.shift(0, -1)));
		assertTrue(ip.intersectInterval(ip.shift(1, -1)));

		assertTrue(ip.intersectInterval(ip.shift(-1, 0)));
		assertTrue(ip.intersectInterval(ip.shift(0, 1)));
		assertTrue(ip.intersectInterval(ip.shift(-1, 1)));

		assertFalse(ip.intersectInterval(ip.shiftStart(-1, 0)));
		assertTrue(ip.intersectInterval(ip.shiftStart(0, 1)));
		assertTrue(ip.intersectInterval(ip.shiftStart(-1, 1)));
		
		assertTrue(ip.intersectInterval(ip.shiftExclusifEnd(-1, 0)));
		assertFalse(ip.intersectInterval(ip.shiftExclusifEnd(0, 1)));
		assertTrue(ip.intersectInterval(ip.shiftExclusifEnd(-1, 1)));
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals"})
	<T extends Comparable<? super T>> void testIsInverted(IntervalShiftingProxy<T> interval) {//increase test cov
	
		assertFalse(interval.isInverted());
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
    		Arguments.arguments(ofPeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 5), DAYS)),
    		Arguments.arguments(ofPeriod(LocalTime.of(0 , 40), LocalTime.of(0, 55), MINUTES)),
    		Arguments.arguments(ofPeriod(LocalDateTime.of(2020, 10, 25, 0 , 40), LocalDateTime.of(2020, 10, 25, 15, 40), HOURS)),
    		Arguments.arguments(ofPeriod(Instant.parse("2020-03-28T02:00:00Z"), Instant.parse("2020-03-28T02:00:05Z"), SECONDS))
	    );
	}

	@SuppressWarnings("unchecked")
	static <T extends Comparable<? super T> & Temporal> IntervalShiftingProxy<T> ofPeriod(T start, T exclusifEnd, ChronoUnit temporalUnit){
		
		return ofInterval(start, exclusifEnd, (o,v)->(T) o.plus(v, temporalUnit));
	}
	
	static <T extends Comparable<? super T>> IntervalShiftingProxy<T> ofInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){
		
		return new IntervalShiftingProxy<>(create(start, exclusifEnd), RegularIntervalTest::create, getFn);
	}
	
	static <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd){
		
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
