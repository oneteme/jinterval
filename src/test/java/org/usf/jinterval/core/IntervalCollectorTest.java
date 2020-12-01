package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class IntervalCollectorTest implements IntervalFactory {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testMaxInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {

		var in = ofInterval(start, exclusifEnd, getFn);
		
		testMaxInterval(Optional.empty());
		testMaxInterval(Optional.of(in), in);
		testMaxInterval(Optional.of(in), in, in);
		
		testMaxInterval(Optional.of(in), in, in.shift(1, 0));
		testMaxInterval(Optional.of(in), in, in.shift(0, -1));
		testMaxInterval(Optional.of(in), in, in.shift(1, -1));
		
		testMaxInterval(Optional.of(in.shift(-1, 0)), in, in.shift(-1, 0));
		testMaxInterval(Optional.of(in.shift(0, 1)), in, in.shift(0, 1));
		testMaxInterval(Optional.of(in.shift(-1, 1)), in, in.shift(-1, 1));
		
		testMaxInterval(Optional.of(in.shift(0, 1)), in, in.shift(1, 1));
		testMaxInterval(Optional.of(in.shift(-1, 0)), in, in.shift(-1, -1));
		
		testMaxInterval(Optional.of(in.shift(-1, 0)), in, in.shiftStart(-1, 0));
		testMaxInterval(Optional.of(in.shift(0, 1)), in, in.shiftExclusifEnd(0, 1));
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testMinInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {

		var in = ofInterval(start, exclusifEnd, getFn);
		
		testMinInterval(Optional.empty());
		testMinInterval(Optional.of(in), in);
		testMinInterval(Optional.of(in), in, in);

		testMinInterval(Optional.of(in.shift(1, 0)), in, in.shift(1, 0));
		testMinInterval(Optional.of(in.shift(0, -1)), in, in.shift(0, -1));
		testMinInterval(Optional.of(in.shift(1, -1)), in, in.shift(1, -1));
		
		testMinInterval(Optional.of(in), in, in.shift(-1, 0));
		testMinInterval(Optional.of(in), in, in.shift(0, 1));
		testMinInterval(Optional.of(in), in, in.shift(-1, 1));

		testMinInterval(Optional.of(in.shift(1, 0)), in, in.shift(1, 1));
		testMinInterval(Optional.of(in.shift(0, -1)), in, in.shift(-1, -1));

		testMinInterval(Optional.empty(), in, in.shiftStart(-1, 0));
		testMinInterval(Optional.empty(), in, in.shiftExclusifEnd(0, 1));
	}
	
	@SafeVarargs
	private <T extends Comparable<? super T>> void testMaxInterval(Optional<Interval<T>> expected, Interval<T>... arr) {
		
		var list = Arrays.asList(arr);
		assertEquals(expected, list.stream().collect(IntervalCollector.maxInterval()));
		Collections.shuffle(list);
		assertEquals(expected, list.stream().collect(IntervalCollector.maxInterval()));
	}

	@SafeVarargs
	private <T extends Comparable<? super T>> void testMinInterval(Optional<Interval<T>> expected, Interval<T>... arr) {
		
		var list = Arrays.asList(arr);
		assertEquals(expected, list.stream().collect(IntervalCollector.minInterval()));
		Collections.shuffle(list);
		assertEquals(expected, list.stream().collect(IntervalCollector.minInterval()));
	}

	@Override
	public <T extends Comparable<? super T>> ImmutableInterval<T> create(T start, T exclusifEnd) {
		return ImmutableInterval.of(start, exclusifEnd);
	}
}
