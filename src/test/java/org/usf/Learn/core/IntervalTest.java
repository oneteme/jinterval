package org.usf.Learn.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.usf.learn.core.Interval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

class IntervalTest {

	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testContainsField(IntervalImpl<T> interval) {

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
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testContainsInterval(IntervalImpl<T> interval) {

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
		
		assertTrue(interval.containsInterval(interval.shiftStart(0, 0))); //??
		assertTrue(interval.containsInterval(interval.shiftExclusifEnd(0, 0))); //??
	}
	
	@ParameterizedTest(name="{0}")
	@MethodSource("caseFactory")
	<T extends Comparable<? super T>> void testIntersectInterval(IntervalImpl<T> interval) {

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

		assertFalse(interval.intersectInterval(interval.shiftStart(0, 0))); //??
		assertFalse(interval.intersectInterval(interval.shiftExclusifEnd(0, 0))); //??
	}

	private static Stream<Arguments> caseFactory() {
	    return Stream.of(
    		Arguments.arguments(new IntervalImpl<>(1, 5, shiftInt)),
    		Arguments.arguments(new IntervalImpl<>(-5L, 5L, shiftLong)),
    		Arguments.arguments(new IntervalImpl<>(-30., -20., shiftDouble)),
    		Arguments.arguments(new IntervalImpl<>(BigDecimal.ONE, BigDecimal.TEN, shiftBigDecimal))
	    );
	}

	private static BinaryOperator<Integer> shiftInt = (a,b)-> a+b;
	private static BiFunction<Long, Integer, Long> shiftLong = (a,b)-> a+b;
	private static BiFunction<Double, Integer, Double> shiftDouble = (a,b)-> a+b;
	private static BiFunction<BigDecimal, Integer, BigDecimal> shiftBigDecimal = (a,b)-> a.add(BigDecimal.valueOf(b));
		

	@Getter
	@RequiredArgsConstructor
	static class IntervalImpl<T extends Comparable<? super T>> implements Interval<T> {
		
		private final T start, exclusifEnd;
		private final BiFunction<T, Integer, T> fn;
		
		final T shiftStart(int v){
			return shift(start, v);
		}
		final T shiftExclusifEnd(int v){
			return shift(exclusifEnd, v);
		}

		final IntervalImpl<T> shift(int v1, int v2){
			return copy(shift(start, v1), shift(exclusifEnd, v2));
		}
		final IntervalImpl<T> shiftStart(int v1, int v2){
			return copy(shift(start, v1), shift(start, v2));
		}
		final IntervalImpl<T> shiftExclusifEnd(int v1, int v2){
			return copy(shift(exclusifEnd, v1), shift(exclusifEnd, v2));
		}

		IntervalImpl<T> copy(T s, T e){
			return new IntervalImpl<>(s, e, fn);
		}
		
		T shift(T obj, int value){
			return fn.apply(obj, value);
		}
		@Override 
		public String toString() {
			return "[" + getStart() + ", " + getExclusifEnd() + "[";
		}
	}

}
