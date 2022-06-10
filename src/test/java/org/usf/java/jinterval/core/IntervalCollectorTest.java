package org.usf.java.jinterval.core;

import static java.time.Month.APRIL;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.java.jinterval.Utils.assertExceptionMsg;

import java.time.LocalTime;
import java.time.Month;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class IntervalCollectorTest  {
	
	@Test
	void testLargestInterval() {
		
		Stream<Interval<Long>> s1 = Stream.empty();
		assertEquals(s1.collect(IntervalCollector.largestInterval()), Optional.empty());

		Stream<Interval<Month>> s2 = Stream.of(new ImmutableInterval<>(JANUARY, APRIL));
		assertEquals(new ImmutableInterval<>(JANUARY, APRIL), s2.collect(IntervalCollector.largestInterval()).get());
		
		var s3 = Stream.of(
				new ImmutableInterval<>(1, 5),
				new ImmutableInterval<>(3, 7),
				new ImmutableInterval<>(-2, 3));
		assertEquals(new ImmutableInterval<>(-2, 7), s3.collect(IntervalCollector.largestInterval()).get());
		
		Stream<ImmutableInterval<LocalTime>> s4 = Stream.of(new ImmutableInterval<>(LocalTime.of(22, 0), LocalTime.of(7, 0)));
		assertExceptionMsg(IllegalArgumentException.class, ()-> s4.collect(IntervalCollector.largestInterval()), "inverted interval");
	}
	
	@Test
	void testSmallestInterval() {
		
		Stream<Interval<Long>> s1 = Stream.empty();
		assertEquals(Optional.empty(), s1.collect(IntervalCollector.smallestInterval()));

		Stream<Interval<Month>> s2 = Stream.of(new ImmutableInterval<>(JANUARY, APRIL));
		assertEquals(new ImmutableInterval<>(JANUARY, APRIL), s2.collect(IntervalCollector.smallestInterval()).get());
		
		var s3 = Stream.of(
				new ImmutableInterval<>(2, 15),
				new ImmutableInterval<>(4, 16),
				new ImmutableInterval<>(-20, 5));
		assertEquals(new ImmutableInterval<>(4, 5), s3.collect(IntervalCollector.smallestInterval()).get());
		
		var s4 = Stream.of(
				new ImmutableInterval<>(Month.JANUARY, Month.MARCH),
				new ImmutableInterval<>(Month.MARCH, Month.SEPTEMBER));
		assertEquals(Optional.empty(), s4.collect(IntervalCollector.smallestInterval()));
		
		Stream<ImmutableInterval<LocalTime>> s5 = Stream.of(new ImmutableInterval<>(LocalTime.of(22, 0), LocalTime.of(7, 0)));
		assertExceptionMsg(IllegalArgumentException.class, ()-> s5.collect(IntervalCollector.largestInterval()), "inverted interval");
	}
	
}
