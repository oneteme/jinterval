package org.usf.jinterval.core;

import java.util.function.BiFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public interface CommonTestInterval2 extends IntervalFactory {

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	default <T extends Comparable<? super T>> void testEquals(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){

		var in = ofInterval(start, exclusifEnd, getFn);

		testEquals(true, null, null);//same
		testEquals(true, in, in);//same
		testEquals(true, in, in.shift(0, 0)); 

		testEquals(false, in, null);
		testEquals(false, in, in.shift(1, 0)); 
		testEquals(false, in, in.shift(-1, 0)); 
		testEquals(false, in, in.shift(0, 1)); 
		testEquals(false, in, in.shift(0, -1));
	}

	@ParameterizedTest(name="[{0}, {1}[")
	@MethodSource({"numberIntervals", "temporalIntervals", "enumIntervals"})
	default <T extends Comparable<? super T>> void testToString(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){

		var in = ofInterval(start, exclusifEnd, getFn);
		testToString("[" + in.getStart() + ", " + in.getExclusifEnd() + "[", in);
	}

	<T extends Comparable<? super T>> void testToString(String expected, Interval<T> ip);

	<T extends Comparable<? super T>> void testEquals(boolean expected, Interval<T> ip, Interval<T> interval);
}
