package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

final class ImmutableIntervalTest extends IntervalTest implements CommonTestInterval2 {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testReverseInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){

		var val = create(start, exclusifEnd);
		var rev = val.reverseInterval();
		assertEquals(val.getStart(), rev.getExclusifEnd());
		assertEquals(val.getExclusifEnd(), rev.getStart());
	}
	
	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	<T extends Comparable<? super T>> void testHashCode(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){

		var in = ofInterval(start, exclusifEnd, getFn);
		
		Set<Interval<T>> intervals = new HashSet<Interval<T>>();
		intervals.add(in);
		intervals.add(in);//same object
		intervals.add(create(start, exclusifEnd));//equals object
		assertEquals(1, intervals.size());
		intervals.add(create(exclusifEnd, start));//invert object
		assertEquals(2, intervals.size());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public <T extends Comparable<? super T>> void testEquals(boolean expected, Interval<T> ip, Interval<T> interval) {
		if(ip != null) {
			assertEquals(expected, ip.equals(interval));
			assertEquals(false, ip.equals(ip.getStart())); //instanceOf
			assertEquals(false, ip.equals(ip.getExclusifEnd())); //instanceOf
		}
		if(interval != null) {
			assertEquals(expected, interval.equals(ip));
			assertEquals(false, interval.equals(interval.getStart())); //instanceOf
			assertEquals(false, interval.equals(interval.getExclusifEnd())); //instanceOf
		}
	}
	
	@Override
	public <T extends Comparable<? super T>> void testToString(String expected, Interval<T> ip) {
		assertEquals(expected, ip.toString());
	}
	
	@Override
	public <T extends Comparable<? super T>> ImmutableInterval<T> create(T start, T exclusifEnd){
		
		return ImmutableInterval.of(start, exclusifEnd);
	}

}
