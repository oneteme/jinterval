package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

final class ImmutableIntervalTest extends IntervalTest {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testToString(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn) {//increase test cov

		var in = ofInterval(start, exclusifEnd, getFn);
		assertEquals("[" + in.getStart() + ", " + in.getExclusifEnd() + "[", in.toString());
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testEquals(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){

		var in = ofInterval(start, exclusifEnd, getFn);
		var val = create(start, exclusifEnd);

		assertTrue(val.equals(val));
		assertTrue(val.equals(in));
		assertFalse(val.equals(null));
		assertFalse(val.equals(create(exclusifEnd, start)));
		
		assertFalse(val.equals(start));
		assertFalse(val.equals(exclusifEnd));
		
		assertFalse(val.equals(in.shift(0, 1)));
		assertFalse(val.equals(in.shift(0, -1)));
		assertFalse(val.equals(in.shift(1, 0)));
		assertFalse(val.equals(in.shift(-1, 0)));
	}

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testHashCode(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){

		var in = ofInterval(start, exclusifEnd, getFn);
		
		Set<Interval<T>> intervals = new HashSet<Interval<T>>();
		intervals.add(in);
		intervals.add(in);
		assertEquals(1, intervals.size());
		intervals.add(create(exclusifEnd, start));
		assertEquals(2, intervals.size());
	}
	
	@Override
	public <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd){
		
		return ImmutableInterval.of(start, exclusifEnd);
	}

}
