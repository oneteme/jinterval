package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RegularIntervalTest {

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals"})
	<T extends Comparable<? super T>> void testContainsField(IntervalShifting<T> interval) {

		assertTrue(interval.containsField(interval.getStart()));
		assertTrue(interval.containsField(interval.shiftStart(1)));
		assertFalse(interval.containsField(interval.shiftStart(-1)));
		
		assertFalse(interval.containsField(interval.getExclusifEnd()));
		assertFalse(interval.containsField(interval.shiftExclusifEnd(1)));
		assertTrue(interval.containsField(interval.shiftExclusifEnd(-1)));
		
		assertFalse(interval.shiftStart(0, 0).containsField(interval.getStart()));
		assertFalse(interval.shiftExclusifEnd(0, 0).containsField(interval.getExclusifEnd()));
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals"})
	<T extends Comparable<? super T>> void testContainsInterval(IntervalShifting<T> interval) {

		assertTrue(interval.containsInterval(interval));

		assertTrue(interval.containsInterval(interval.shift(1, 0)));
		assertTrue(interval.containsInterval(interval.shift(0, -1)));
		assertTrue(interval.containsInterval(interval.shift(1, -1)));

		assertFalse(interval.containsInterval(interval.shift(-1, 0)));
		assertFalse(interval.containsInterval(interval.shift(0, 1)));
		assertFalse(interval.containsInterval(interval.shift(-1, 1)));

		assertFalse(interval.containsInterval(interval.shiftStart(-1, 0)));
		assertTrue(interval.containsInterval(interval.shiftStart(0, 1)));
		assertFalse(interval.containsInterval(interval.shiftStart(-1, 1)));
		
		assertFalse(interval.containsInterval(interval.shiftExclusifEnd(0, 1)));
		assertTrue(interval.containsInterval(interval.shiftExclusifEnd(-1, 0)));
		assertFalse(interval.containsInterval(interval.shiftExclusifEnd(-1, 1)));
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals"})
	<T extends Comparable<? super T>> void testIntersectInterval(IntervalShifting<T> interval) {

		assertTrue(interval.intersectInterval(interval));

		assertTrue(interval.intersectInterval(interval.shift(1, 0)));
		assertTrue(interval.intersectInterval(interval.shift(0, -1)));
		assertTrue(interval.intersectInterval(interval.shift(1, -1)));

		assertTrue(interval.intersectInterval(interval.shift(-1, 0)));
		assertTrue(interval.intersectInterval(interval.shift(0, 1)));
		assertTrue(interval.intersectInterval(interval.shift(-1, 1)));

		assertFalse(interval.intersectInterval(interval.shiftStart(-1, 0)));
		assertTrue(interval.intersectInterval(interval.shiftStart(0, 1)));
		assertTrue(interval.intersectInterval(interval.shiftStart(-1, 1)));
		
		assertFalse(interval.intersectInterval(interval.shiftExclusifEnd(0, 1)));
		assertTrue(interval.intersectInterval(interval.shiftExclusifEnd(-1, 0)));
		assertTrue(interval.intersectInterval(interval.shiftExclusifEnd(-1, 1)));
	}

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "periodIntervals"})
	<T extends Comparable<? super T>> void testToString(IntervalShifting<T> interval) {//increase test cov
		
		assertEquals("[" + interval.getStart() + ", " + interval.getExclusifEnd() + "[", interval.toString());
	}

	static Stream<Arguments> numberIntervals() {
	    return Stream.of(
    		Arguments.arguments(new RegularIntervalImpl<>(1, 5, shiftInt)),
    		Arguments.arguments(new RegularIntervalImpl<>(-5L, 5L, shiftLong)),
    		Arguments.arguments(new RegularIntervalImpl<>(-30., -20., shiftDouble)),
    		Arguments.arguments(new RegularIntervalImpl<>(BigDecimal.ONE, BigDecimal.TEN, shiftBigDecimal))
	    );
	}

	static Stream<Arguments> periodIntervals() {
		 return Stream.of(
    		Arguments.arguments(ofPeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 5), ChronoUnit.DAYS)),
    		Arguments.arguments(ofPeriod(LocalTime.of(0 , 40), LocalTime.of(0, 55), ChronoUnit.MINUTES)),
    		Arguments.arguments(ofPeriod(LocalDateTime.of(2020, 10, 25, 0 , 40), LocalDateTime.of(2020, 10, 25, 15, 40), ChronoUnit.HOURS)),
    		Arguments.arguments(ofPeriod(Instant.parse("2020-03-28T02:00:00Z"), Instant.parse("2020-03-28T02:01:00Z"), ChronoUnit.SECONDS))
	    );
	}

	@SuppressWarnings("unchecked")
	private static <T extends Comparable<? super T> & Temporal> RegularIntervalImpl<T> ofPeriod(T start, T exclusifEnd, ChronoUnit temporalUnit){
		
		return new RegularIntervalImpl<>(start, exclusifEnd, (o,v)->(T) o.plus(v, temporalUnit));
	}

	private static BinaryOperator<Integer> shiftInt = (a,b)-> a+b;
	private static BiFunction<Long, Integer, Long> shiftLong = (a,b)-> a+b;
	private static BiFunction<Double, Integer, Double> shiftDouble = (a,b)-> a+b;
	private static BiFunction<BigDecimal, Integer, BigDecimal> shiftBigDecimal = (a,b)-> a.add(BigDecimal.valueOf(b));
}
