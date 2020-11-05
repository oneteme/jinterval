package org.usf.Learn.core;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.usf.learn.core.Interval;
import org.usf.learn.core.IntervalCollection;
import org.usf.learn.exception.MissingIntervalException;
import org.usf.learn.exception.OverlapIntervalException;

class IntervalCollectionTest {

	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testMissingIntervals(IntervalImpl<T> interval) {
		
		testMissingInterval(emptyList(), emptyList(), interval);
		testMissingInterval(singletonList(interval), emptyList(), interval);
		testMissingInterval(asList(interval, interval), emptyList(), interval);

		testMissingInterval(asList(interval, interval.prev(-1, 0)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.next(0, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.prev(-1, 0), interval.next(0, 1)), emptyList(), interval);

		testMissingInterval(asList(interval, interval.prev(0, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.next(-1, 0)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.prev(0, 1), interval.next(-1, 0)), emptyList(), interval);
		
		testMissingInterval(asList(interval, interval.prev(-1, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.next(-1, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.prev(-1, 1), interval.next(-1, 1)), emptyList(), interval);
		
		testMissingInterval(asList(interval, interval.prev(-5, 2), interval.next(2, 10)), singletonList(interval.next(0, 2)), interval);
		testMissingInterval(asList(interval, interval.prev(-10, -2), interval.next(-2, 5)), singletonList(interval.prev(-2, 0)), interval);
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testOverlapIntervals(IntervalImpl<T> interval) {
		
		testOverlapInterval(emptyList(), emptyList(), interval);
		testOverlapInterval(singletonList(interval), emptyList(), interval);
		testOverlapInterval(asList(interval, interval), singletonList(interval), interval);

		testOverlapInterval(asList(interval, interval.prev(-1, 0)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.next(0, 1)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.prev(-1, 0), interval.next(0, 1)), emptyList(), interval);

		testOverlapInterval(asList(interval, interval.prev(0, 1)), asList(interval.prev(0, 1)), interval);
		testOverlapInterval(asList(interval, interval.next(-1, 0)), asList(interval.next(-1, 0)), interval);
		testOverlapInterval(asList(interval, interval.prev(0, 1), interval.next(-1, 0)), asList(interval.prev(0, 1), interval.next(-1, 0)), interval);
		
		testOverlapInterval(asList(interval, interval.prev(-1, 1)), asList(interval.prev(0, 1)), interval);
		testOverlapInterval(asList(interval, interval.next(-1, 1)), asList(interval.next(-1, 0)), interval);
		testOverlapInterval(asList(interval, interval.prev(-1, 1), interval.next(-1, 1)), asList(interval.prev(0, 1), interval.next(-1, 0)), interval);
		
		testOverlapInterval(asList(interval, interval.prev(-2, -1)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.next(1, 2)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.next(1, 2), interval.prev(-2, -1)), emptyList(), interval);

		testOverlapInterval(asList(interval, interval.prev(-5, 2), interval.next(2, 10)), singletonList(interval.prev(0, 2)), interval);
		testOverlapInterval(asList(interval, interval.prev(-10, -2), interval.next(-2, 5)), singletonList(interval.next(-2, 0)), interval);
	}
	
	private <T extends Comparable<? super T>> void testMissingInterval(List<IntervalImpl<T>> intervals, List<IntervalImpl<T>> expectedIntervals, IntervalImpl<T> origin) {
		
		IntervalCollection<T> ic = new IntervalCollection<>(intervals);
		T min = intervals.stream().map(Interval::getStart).min(Comparator.naturalOrder()).orElse(null);
		T max = intervals.stream().map(Interval::getExclusifEnd).max(Comparator.naturalOrder()).orElse(null);
		
		assertEquals(!expectedIntervals.isEmpty(), ic.isMissingIntervals());
		assertEquals(!expectedIntervals.isEmpty(), ic.isMissingIntervals(null, null));
		assertEquals(!expectedIntervals.isEmpty(), ic.isMissingIntervals(min, max));
		
		assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals().toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals(null, null).toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals(min, max).toArray());
		
		assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(origin::create, Collectors.toList()).toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(null, null, origin::create, Collectors.toList()).toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(min, max, origin::create, Collectors.toList()).toArray());
		
		if(!expectedIntervals.isEmpty()) {
			Utils.assertException(MissingIntervalException.class, ()-> ic.requiredNotMissingIntervals(), "missing interval");
			Utils.assertException(MissingIntervalException.class, ()-> ic.requiredNotMissingIntervals(min, max), "missing interval");
		}
		else {
			assertDoesNotThrow(()-> ic.requiredNotMissingIntervals());
			assertDoesNotThrow(()-> ic.requiredNotMissingIntervals(min, max));
		}
		if(!intervals.isEmpty()) {
			IntervalImpl<T> outInterval = origin.create(min, max).shift(-1, 1);
			expectedIntervals = new ArrayList<>(expectedIntervals);
			expectedIntervals.add(0, outInterval.shiftStart(0, 1));
			expectedIntervals.add(outInterval.shiftExclusifEnd(-1, 0));
			assertEquals(true, ic.isMissingIntervals(outInterval.getStart(), outInterval.getExclusifEnd()));
			assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals(outInterval.getStart(), outInterval.getExclusifEnd()).toArray());
			assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(outInterval.getStart(), outInterval.getExclusifEnd(), origin::create, Collectors.toList()).toArray());
			Utils.assertException(MissingIntervalException.class, ()-> ic.requiredNotMissingIntervals(outInterval.getStart(), outInterval.getExclusifEnd()), "missing interval");
		}
	}
	
	private <T extends Comparable<? super T>> void testOverlapInterval(List<IntervalImpl<T>> intervals, List<IntervalImpl<T>> expectedIntervals, IntervalImpl<T> origin) {
		
		IntervalCollection<T> ic = new IntervalCollection<>(intervals);
		
		assertEquals(!expectedIntervals.isEmpty(), ic.isOverlapIntervals());
		assertArrayEquals(expectedIntervals.toArray(), ic.overlapIntervals().toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.collectOverlapIntervals(origin::create, Collectors.toList()).toArray());

		if(!expectedIntervals.isEmpty()) {
			Utils.assertException(OverlapIntervalException.class, ()-> ic.requiredNotOverlapIntervals(), "overlap interval");
		}
		else {
			assertDoesNotThrow(()-> ic.requiredNotOverlapIntervals());
		}
	}

	static Stream<Arguments> caseFactory() {
		 return Stream.concat(IntervalTest.caseFactory(), PeriodTest.caseFactory());
	}
}
