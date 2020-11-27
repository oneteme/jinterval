package org.usf.jinterval.core;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.jinterval.Utils.assertExceptionMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.usf.jinterval.Utils;
import org.usf.jinterval.core.exception.MissingIntervalException;
import org.usf.jinterval.core.exception.OverlapIntervalException;

class IntervalCollectionTest {

	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testMaxInterval(IntervalShiftingProxy<T> interval) {

		testMaxInterval(Collections.emptyList(), null);
		testMaxInterval(Arrays.asList(interval), interval);
		testMaxInterval(Arrays.asList(interval, interval.shiftExclusifEnd(3, 5), interval.shiftStart(-5, -3)), interval.shift(-5, 5));
		testMaxInterval(Arrays.asList(interval, interval.shiftExclusifEnd(0, 3), interval.shiftStart(-3, 0)), interval.shift(-3, 3));
		testMaxInterval(Arrays.asList(interval, interval.shift(1, 2), interval.shift(-5, -2)), interval.shift(-5, 2));
		testMaxInterval(Arrays.asList(interval, interval.shiftStart(0, 3), interval.shiftStart(-5, 2)), interval.shift(-5, 0));
		testMaxInterval(Arrays.asList(interval, interval.shiftExclusifEnd(-3, 3), interval.shiftExclusifEnd(-2, 5)), interval.shift(0, 5));
	}

	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testMinInterval(IntervalShiftingProxy<T> interval) {

		testMinInterval(Collections.emptyList(), null);
		testMinInterval(Arrays.asList(interval), interval);
		testMinInterval(Arrays.asList(interval, interval.shiftExclusifEnd(3, 5), interval.shiftStart(-5, -3)), null);
		testMinInterval(Arrays.asList(interval, interval.shiftExclusifEnd(0, 3), interval.shiftStart(-3, 0)), null);
//		testMinInterval(Arrays.asList(interval, interval.shift(1, 2), interval.shift(-5, -2)), null);
		testMinInterval(Arrays.asList(interval, interval.shiftStart(0, 3), interval.shiftStart(-5, 2)), interval.shiftStart(0, 2));
		testMinInterval(Arrays.asList(interval, interval.shiftExclusifEnd(-3, 3), interval.shiftExclusifEnd(-2, 5)), interval.shiftExclusifEnd(-2, 0));
		
	}

	<T extends Comparable<? super T>> void testMinInterval(List<Interval<T>> intervals, Interval<T> expexted) {
			
		var res = new IntervalCollection<>(intervals).minInterval();
		if(expexted == null) {
			assertTrue(res.isEmpty());
		}
		else {
			assertEquals(expexted, ImmutableInterval.of(res.get().getStart(), res.get().getExclusifEnd()));
		}
	}
	
	<T extends Comparable<? super T>> void testMaxInterval(List<Interval<T>> intervals, Interval<T> expexted) {
			
		var res = new IntervalCollection<>(intervals).maxInterval();
		if(expexted == null) {
			assertTrue(res.isEmpty());
		}
		else {
			assertEquals(expexted, ImmutableInterval.of(res.get().getStart(), res.get().getExclusifEnd()));
		}
	}

	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testMissingIntervals(IntervalShiftingProxy<T> interval) {
		
		testMissingInterval(emptyList(), emptyList(), interval);
		testMissingInterval(singletonList(interval), emptyList(), interval);
		testMissingInterval(asList(interval, interval), emptyList(), interval);

		testMissingInterval(asList(interval, interval.shiftStart(-1, 0)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.shiftExclusifEnd(0, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.shiftStart(-1, 0), interval.shiftExclusifEnd(0, 1)), emptyList(), interval);

		testMissingInterval(asList(interval, interval.shiftStart(-2, -1)), singletonList(interval.shiftStart(-1, 0)), interval);
		testMissingInterval(asList(interval, interval.shiftExclusifEnd(1, 2)), singletonList(interval.shiftExclusifEnd(0, 1)), interval);
		testMissingInterval(asList(interval, interval.shiftStart(-2, -1), interval.shiftExclusifEnd(1, 2)), asList(interval.shiftStart(-1, 0), interval.shiftExclusifEnd(0, 1)), interval);

		testMissingInterval(asList(interval, interval.shiftStart(0, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.shiftExclusifEnd(-1, 0)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.shiftStart(0, 1), interval.shiftExclusifEnd(-1, 0)), emptyList(), interval);
		
		testMissingInterval(asList(interval, interval.shiftStart(-1, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.shiftExclusifEnd(-1, 1)), emptyList(), interval);
		testMissingInterval(asList(interval, interval.shiftStart(-1, 1), interval.shiftExclusifEnd(-1, 1)), emptyList(), interval);
		
		testMissingInterval(asList(interval, interval.shiftStart(-5, -2), interval.shiftExclusifEnd(-2, 10)), singletonList(interval.shiftStart(-2, 0)), interval);
		testMissingInterval(asList(interval, interval.shiftStart(-10, 2), interval.shiftExclusifEnd(2, 5)), singletonList(interval.shiftExclusifEnd(0, 2)), interval);
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testOverlapIntervals(IntervalShiftingProxy<T> interval) {
		
		testOverlapInterval(emptyList(), emptyList(), interval);
		testOverlapInterval(singletonList(interval), emptyList(), interval);
		testOverlapInterval(asList(interval, interval), singletonList(interval), interval);

		testOverlapInterval(asList(interval, interval.shiftStart(-1, 0)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.shiftExclusifEnd(0, 1)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.shiftStart(-1, 0), interval.shiftExclusifEnd(0, 1)), emptyList(), interval);

		testOverlapInterval(asList(interval, interval.shiftStart(-2, -1)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.shiftExclusifEnd(1, 2)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.shiftStart(-2, -1), interval.shiftExclusifEnd(1, 2)), emptyList(), interval);

		testOverlapInterval(asList(interval, interval.shiftStart(0, 1)), asList(interval.shiftStart(0, 1)), interval);
		testOverlapInterval(asList(interval, interval.shiftExclusifEnd(-1, 0)), asList(interval.shiftExclusifEnd(-1, 0)), interval);
		testOverlapInterval(asList(interval, interval.shiftStart(0, 1), interval.shiftExclusifEnd(-1, 0)), asList(interval.shiftStart(0, 1), interval.shiftExclusifEnd(-1, 0)), interval);
		
		testOverlapInterval(asList(interval, interval.shiftStart(-1, 1)), asList(interval.shiftStart(0, 1)), interval);
		testOverlapInterval(asList(interval, interval.shiftExclusifEnd(-1, 1)), asList(interval.shiftExclusifEnd(-1, 0)), interval);
		testOverlapInterval(asList(interval, interval.shiftStart(-1, 1), interval.shiftExclusifEnd(-1, 1)), asList(interval.shiftStart(0, 1), interval.shiftExclusifEnd(-1, 0)), interval);
		
		testOverlapInterval(asList(interval, interval.shiftStart(-2, -1)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.shiftExclusifEnd(1, 2)), emptyList(), interval);
		testOverlapInterval(asList(interval, interval.shiftExclusifEnd(1, 2), interval.shiftStart(-2, -1)), emptyList(), interval);

		testOverlapInterval(asList(interval, interval.shiftStart(-5, -2), interval.shiftExclusifEnd(-2, 10)), singletonList(interval.shiftExclusifEnd(-2, 0)), interval);
		testOverlapInterval(asList(interval, interval.shiftStart(-10, 2), interval.shiftExclusifEnd(2, 5)), singletonList(interval.shiftStart(0, 2)), interval);
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testLinkedIntervals(IntervalShiftingProxy<T> interval) {
		
		testLinkedInterval(emptyList(), emptyList(), null, null, interval);
		testLinkedInterval(singletonList(interval), emptyList(), null, null, interval);
		testLinkedInterval(asList(interval, interval), singletonList(interval), "overlap interval", OverlapIntervalException.class, interval);

		testLinkedInterval(asList(interval, interval.shiftStart(-1, 0)), emptyList(), null, null, interval);
		testLinkedInterval(asList(interval, interval.shiftExclusifEnd(0, 1)), emptyList(), null, null, interval);
		testLinkedInterval(asList(interval, interval.shiftStart(-1, 0), interval.shiftExclusifEnd(0, 1)), emptyList(), null, null, interval);

		testLinkedInterval(asList(interval, interval.shiftStart(-2, -1)), singletonList(interval.shiftStart(-1, 0)), "missing interval", MissingIntervalException.class, interval);
		testLinkedInterval(asList(interval, interval.shiftExclusifEnd(1, 2)), singletonList(interval.shiftExclusifEnd(0, 1)), "missing interval", MissingIntervalException.class, interval);
		testLinkedInterval(asList(interval, interval.shiftStart(-2, -1), interval.shiftExclusifEnd(1, 2)), asList(interval.shiftStart(-1, 0), interval.shiftExclusifEnd(0, 1)), "missing interval", MissingIntervalException.class, interval);

		testLinkedInterval(asList(interval, interval.shiftStart(0, 1)), asList(interval.shiftStart(0, 1)), "overlap interval", OverlapIntervalException.class, interval);
		testLinkedInterval(asList(interval, interval.shiftExclusifEnd(-1, 0)), asList(interval.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, interval);
		testLinkedInterval(asList(interval, interval.shiftStart(0, 1), interval.shiftExclusifEnd(-1, 0)), asList(interval.shiftStart(0, 1), interval.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, interval);
		
		testLinkedInterval(asList(interval, interval.shiftStart(-1, 1)), asList(interval.shiftStart(0, 1)), "overlap interval", OverlapIntervalException.class, interval);
		testLinkedInterval(asList(interval, interval.shiftExclusifEnd(-1, 1)), asList(interval.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, interval);
		testLinkedInterval(asList(interval, interval.shiftStart(-1, 1), interval.shiftExclusifEnd(-1, 1)), asList(interval.shiftStart(0, 1), interval.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, interval);
		
		testLinkedInterval(asList(interval, interval.shiftStart(-5, -2), interval.shiftExclusifEnd(-2, 10)), asList(interval.shiftStart(-2, 0), interval.shiftExclusifEnd(-2, 0)), "missing interval", MissingIntervalException.class, interval);
		testLinkedInterval(asList(interval, interval.shiftStart(-10, 2), interval.shiftExclusifEnd(2, 5)), asList(interval.shiftStart(0, 2), interval.shiftExclusifEnd(0, 2)), "overlap interval", OverlapIntervalException.class, interval);
	}
	
	
	private <T extends Comparable<? super T>> void testMissingInterval(List<Interval<T>> intervals, List<Interval<T>> expectedIntervals, IntervalShiftingProxy<T> origin) {
		
		IntervalCollection<T> ic = new IntervalCollection<>(intervals);
		T min = intervals.stream().map(Interval::getStart).min(Comparator.naturalOrder()).orElse(null);
		T max = intervals.stream().map(Interval::getExclusifEnd).max(Comparator.naturalOrder()).orElse(null);
		
		assertEquals(!expectedIntervals.isEmpty(), ic.isMissingIntervals());
		assertEquals(!expectedIntervals.isEmpty(), ic.isMissingIntervals(null, null));
		assertEquals(!expectedIntervals.isEmpty(), ic.isMissingIntervals(min, max));
		
		assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals().toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals(null, null).toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals(min, max).toArray());
		
		assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(origin.getFactory(), Collectors.toList()).toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(null, null, origin.getFactory(), Collectors.toList()).toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(min, max, origin.getFactory(), Collectors.toList()).toArray());
		
		if(!expectedIntervals.isEmpty()) {
			Utils.assertExceptionMsg(MissingIntervalException.class, ()-> ic.requiredNotMissingIntervals(), "missing interval");
			Utils.assertExceptionMsg(MissingIntervalException.class, ()-> ic.requiredNotMissingIntervals(min, max), "missing interval");
		}
		else {
			assertDoesNotThrow(()-> ic.requiredNotMissingIntervals());
			assertDoesNotThrow(()-> ic.requiredNotMissingIntervals(min, max));
		}
		if(!intervals.isEmpty()) {
			IntervalShiftingProxy<T> outInterval = origin.copy(min, max).shift(-1, 1);
			expectedIntervals = new ArrayList<>(expectedIntervals);
			expectedIntervals.add(0, outInterval.shiftStart(0, 1));
			expectedIntervals.add(outInterval.shiftExclusifEnd(-1, 0));
			assertEquals(true, ic.isMissingIntervals(outInterval.getStart(), outInterval.getExclusifEnd()));
			assertArrayEquals(expectedIntervals.toArray(), ic.missingIntervals(outInterval.getStart(), outInterval.getExclusifEnd()).toArray());
			assertArrayEquals(expectedIntervals.toArray(), ic.collectMissingIntervals(outInterval.getStart(), outInterval.getExclusifEnd(), origin.getFactory(), Collectors.toList()).toArray());
			assertExceptionMsg(MissingIntervalException.class, ()-> ic.requiredNotMissingIntervals(outInterval.getStart(), outInterval.getExclusifEnd()), "missing interval");
		}
	}
	
	private <T extends Comparable<? super T>> void testOverlapInterval(List<Interval<T>> intervals, List<Interval<T>> expectedIntervals, IntervalShiftingProxy<T> origin) {
		
		IntervalCollection<T> ic = new IntervalCollection<>(intervals);
		
		assertEquals(!expectedIntervals.isEmpty(), ic.isOverlapIntervals());
		assertArrayEquals(expectedIntervals.toArray(), ic.overlapIntervals().toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.collectOverlapIntervals(origin.getFactory(), Collectors.toList()).toArray());

		if(!expectedIntervals.isEmpty()) {
			assertExceptionMsg(OverlapIntervalException.class, ()-> ic.requiredNotOverlapIntervals(), "overlap interval");
		}
		else {
			assertDoesNotThrow(()-> ic.requiredNotOverlapIntervals());
		}
	}
	
	private <T extends Comparable<? super T>> void testLinkedInterval(List<Interval<T>> intervals, List<Interval<T>> expectedIntervals,
			
			String exMessage, Class<? extends RuntimeException> clazz, IntervalShiftingProxy<T> origin) {
		
		IntervalCollection<T> ic = new IntervalCollection<>(intervals);
		
		assertEquals(exMessage == null, ic.isLinkedIntervals());
		assertArrayEquals(expectedIntervals.toArray(), ic.dirtyIntervals().toArray());
		assertArrayEquals(expectedIntervals.toArray(), ic.collectDirtyIntervals(origin.getFactory(), Collectors.toList()).toArray());
		
		if(exMessage != null) {
			assertExceptionMsg(clazz, ()-> ic.requiredLinkedIntervals(), exMessage);
		}
		else {
			assertDoesNotThrow(()-> ic.requiredLinkedIntervals());
		}
	}

	static Stream<Arguments> caseFactory() {
		 return Stream.concat(RegularIntervalTest.numberIntervals(), RegularIntervalTest.periodIntervals());
	}
}
