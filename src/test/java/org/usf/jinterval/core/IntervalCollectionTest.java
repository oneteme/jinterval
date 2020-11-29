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
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.usf.jinterval.Utils;
import org.usf.jinterval.core.exception.MissingIntervalException;
import org.usf.jinterval.core.exception.OverlapIntervalException;

class IntervalCollectionTest implements IntervalFactory {

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "temporalIntervals"})
	<T extends Comparable<? super T>> void testMaxInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);

		testMaxInterval(Collections.emptyList(), null);
		testMaxInterval(Arrays.asList(in), in);
		testMaxInterval(Arrays.asList(in, in.shiftExclusifEnd(3, 5), in.shiftStart(-5, -3)), in.shift(-5, 5));
		testMaxInterval(Arrays.asList(in, in.shiftExclusifEnd(0, 3), in.shiftStart(-3, 0)), in.shift(-3, 3));
		testMaxInterval(Arrays.asList(in, in.shift(1, 2), in.shift(-5, -2)), in.shift(-5, 2));
		testMaxInterval(Arrays.asList(in, in.shiftStart(0, 3), in.shiftStart(-5, 2)), in.shift(-5, 0));
		testMaxInterval(Arrays.asList(in, in.shiftExclusifEnd(-3, 3), in.shiftExclusifEnd(-2, 5)), in.shift(0, 5));
	}

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "temporalIntervals"})
	<T extends Comparable<? super T>> void testMinInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);

		testMinInterval(Collections.emptyList(), null);
		testMinInterval(Arrays.asList(in), in);
		testMinInterval(Arrays.asList(in, in.shiftExclusifEnd(3, 5), in.shiftStart(-5, -3)), null);
		testMinInterval(Arrays.asList(in, in.shiftExclusifEnd(0, 3), in.shiftStart(-3, 0)), null);
//		testMinInterval(Arrays.asList(interval, interval.shift(1, 2), interval.shift(-5, -2)), null);
		testMinInterval(Arrays.asList(in, in.shiftStart(0, 3), in.shiftStart(-5, 2)), in.shiftStart(0, 2));
		testMinInterval(Arrays.asList(in, in.shiftExclusifEnd(-3, 3), in.shiftExclusifEnd(-2, 5)), in.shiftExclusifEnd(-2, 0));
		
	}

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "temporalIntervals"})
	<T extends Comparable<? super T>> void testMissingIntervals(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);

		testMissingInterval(emptyList(), emptyList(), in);
		testMissingInterval(singletonList(in), emptyList(), in);
		testMissingInterval(asList(in, in), emptyList(), in);

		testMissingInterval(asList(in, in.shiftStart(-1, 0)), emptyList(), in);
		testMissingInterval(asList(in, in.shiftExclusifEnd(0, 1)), emptyList(), in);
		testMissingInterval(asList(in, in.shiftStart(-1, 0), in.shiftExclusifEnd(0, 1)), emptyList(), in);

		testMissingInterval(asList(in, in.shiftStart(-2, -1)), singletonList(in.shiftStart(-1, 0)), in);
		testMissingInterval(asList(in, in.shiftExclusifEnd(1, 2)), singletonList(in.shiftExclusifEnd(0, 1)), in);
		testMissingInterval(asList(in, in.shiftStart(-2, -1), in.shiftExclusifEnd(1, 2)), asList(in.shiftStart(-1, 0), in.shiftExclusifEnd(0, 1)), in);

		testMissingInterval(asList(in, in.shiftStart(0, 1)), emptyList(), in);
		testMissingInterval(asList(in, in.shiftExclusifEnd(-1, 0)), emptyList(), in);
		testMissingInterval(asList(in, in.shiftStart(0, 1), in.shiftExclusifEnd(-1, 0)), emptyList(), in);
		
		testMissingInterval(asList(in, in.shiftStart(-1, 1)), emptyList(), in);
		testMissingInterval(asList(in, in.shiftExclusifEnd(-1, 1)), emptyList(), in);
		testMissingInterval(asList(in, in.shiftStart(-1, 1), in.shiftExclusifEnd(-1, 1)), emptyList(), in);
		
		testMissingInterval(asList(in, in.shiftStart(-5, -2), in.shiftExclusifEnd(-2, 10)), singletonList(in.shiftStart(-2, 0)), in);
		testMissingInterval(asList(in, in.shiftStart(-10, 2), in.shiftExclusifEnd(2, 5)), singletonList(in.shiftExclusifEnd(0, 2)), in);
	}

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "temporalIntervals"})
	<T extends Comparable<? super T>> void testOverlapIntervals(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);
		
		testOverlapInterval(emptyList(), emptyList(), in);
		testOverlapInterval(singletonList(in), emptyList(), in);
		testOverlapInterval(asList(in, in), singletonList(in), in);

		testOverlapInterval(asList(in, in.shiftStart(-1, 0)), emptyList(), in);
		testOverlapInterval(asList(in, in.shiftExclusifEnd(0, 1)), emptyList(), in);
		testOverlapInterval(asList(in, in.shiftStart(-1, 0), in.shiftExclusifEnd(0, 1)), emptyList(), in);

		testOverlapInterval(asList(in, in.shiftStart(-2, -1)), emptyList(), in);
		testOverlapInterval(asList(in, in.shiftExclusifEnd(1, 2)), emptyList(), in);
		testOverlapInterval(asList(in, in.shiftStart(-2, -1), in.shiftExclusifEnd(1, 2)), emptyList(), in);

		testOverlapInterval(asList(in, in.shiftStart(0, 1)), asList(in.shiftStart(0, 1)), in);
		testOverlapInterval(asList(in, in.shiftExclusifEnd(-1, 0)), asList(in.shiftExclusifEnd(-1, 0)), in);
		testOverlapInterval(asList(in, in.shiftStart(0, 1), in.shiftExclusifEnd(-1, 0)), asList(in.shiftStart(0, 1), in.shiftExclusifEnd(-1, 0)), in);
		
		testOverlapInterval(asList(in, in.shiftStart(-1, 1)), asList(in.shiftStart(0, 1)), in);
		testOverlapInterval(asList(in, in.shiftExclusifEnd(-1, 1)), asList(in.shiftExclusifEnd(-1, 0)), in);
		testOverlapInterval(asList(in, in.shiftStart(-1, 1), in.shiftExclusifEnd(-1, 1)), asList(in.shiftStart(0, 1), in.shiftExclusifEnd(-1, 0)), in);
		
		testOverlapInterval(asList(in, in.shiftStart(-2, -1)), emptyList(), in);
		testOverlapInterval(asList(in, in.shiftExclusifEnd(1, 2)), emptyList(), in);
		testOverlapInterval(asList(in, in.shiftExclusifEnd(1, 2), in.shiftStart(-2, -1)), emptyList(), in);

		testOverlapInterval(asList(in, in.shiftStart(-5, -2), in.shiftExclusifEnd(-2, 10)), singletonList(in.shiftExclusifEnd(-2, 0)), in);
		testOverlapInterval(asList(in, in.shiftStart(-10, 2), in.shiftExclusifEnd(2, 5)), singletonList(in.shiftStart(0, 2)), in);
	}

	@ParameterizedTest(name="{0}")
	@MethodSource({"numberIntervals", "temporalIntervals"})
	<T extends Comparable<? super T>> void testLinkedIntervals(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {
		
		var in = ofInterval(start, exclusifEnd, getFn);

		testLinkedInterval(emptyList(), emptyList(), null, null, in);
		testLinkedInterval(singletonList(in), emptyList(), null, null, in);
		testLinkedInterval(asList(in, in), singletonList(in), "overlap interval", OverlapIntervalException.class, in);

		testLinkedInterval(asList(in, in.shiftStart(-1, 0)), emptyList(), null, null, in);
		testLinkedInterval(asList(in, in.shiftExclusifEnd(0, 1)), emptyList(), null, null, in);
		testLinkedInterval(asList(in, in.shiftStart(-1, 0), in.shiftExclusifEnd(0, 1)), emptyList(), null, null, in);

		testLinkedInterval(asList(in, in.shiftStart(-2, -1)), singletonList(in.shiftStart(-1, 0)), "missing interval", MissingIntervalException.class, in);
		testLinkedInterval(asList(in, in.shiftExclusifEnd(1, 2)), singletonList(in.shiftExclusifEnd(0, 1)), "missing interval", MissingIntervalException.class, in);
		testLinkedInterval(asList(in, in.shiftStart(-2, -1), in.shiftExclusifEnd(1, 2)), asList(in.shiftStart(-1, 0), in.shiftExclusifEnd(0, 1)), "missing interval", MissingIntervalException.class, in);

		testLinkedInterval(asList(in, in.shiftStart(0, 1)), asList(in.shiftStart(0, 1)), "overlap interval", OverlapIntervalException.class, in);
		testLinkedInterval(asList(in, in.shiftExclusifEnd(-1, 0)), asList(in.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, in);
		testLinkedInterval(asList(in, in.shiftStart(0, 1), in.shiftExclusifEnd(-1, 0)), asList(in.shiftStart(0, 1), in.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, in);
		
		testLinkedInterval(asList(in, in.shiftStart(-1, 1)), asList(in.shiftStart(0, 1)), "overlap interval", OverlapIntervalException.class, in);
		testLinkedInterval(asList(in, in.shiftExclusifEnd(-1, 1)), asList(in.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, in);
		testLinkedInterval(asList(in, in.shiftStart(-1, 1), in.shiftExclusifEnd(-1, 1)), asList(in.shiftStart(0, 1), in.shiftExclusifEnd(-1, 0)), "overlap interval", OverlapIntervalException.class, in);
		
		testLinkedInterval(asList(in, in.shiftStart(-5, -2), in.shiftExclusifEnd(-2, 10)), asList(in.shiftStart(-2, 0), in.shiftExclusifEnd(-2, 0)), "missing interval", MissingIntervalException.class, in);
		testLinkedInterval(asList(in, in.shiftStart(-10, 2), in.shiftExclusifEnd(2, 5)), asList(in.shiftStart(0, 2), in.shiftExclusifEnd(0, 2)), "overlap interval", OverlapIntervalException.class, in);
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

	@Override
	public <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd){
		
		return ImmutableInterval.of(start, exclusifEnd);
	}
}
