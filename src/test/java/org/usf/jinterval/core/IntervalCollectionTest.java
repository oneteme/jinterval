package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.usf.jinterval.Utils;
import org.usf.jinterval.core.exception.MissingIntervalException;
import org.usf.jinterval.core.exception.OverlapIntervalException;

class IntervalCollectionTest implements IntervalCollectionFactory {
	
	@ParameterizedTest()
	@MethodSource({"dayOfWeek", "localDate"})
	<T extends Comparable<? super T>> void testMaxInterval(IntervalCollectionTestCase<T> tc) {

		var c = new IntervalCollection<>(tc.cyclic, tc.intervals);
		if(tc.cyclic) {
			Utils.assertExceptionMsg(UnsupportedOperationException.class, ()-> c.maxInterval(), null);
		}
		else {
			Optional<Interval<T>> v = assertDoesNotThrow(()-> c.maxInterval());
			assertEquals(tc.max == null, v.isEmpty());
			if(tc.max != null) {
				assertEquals(tc.max, v.get());
			}
		}
	}
	
	@ParameterizedTest()
	@MethodSource({"dayOfWeek", "localDate"})
	<T extends Comparable<? super T>> void testMinInterval(IntervalCollectionTestCase<T> tc) {

		var c = new IntervalCollection<>(tc.cyclic, tc.intervals);
		if(tc.cyclic) {
			Utils.assertExceptionMsg(UnsupportedOperationException.class, ()-> c.minInterval(), null);
		}
		else {
			Optional<Interval<T>> v = assertDoesNotThrow(()-> c.minInterval());
			assertEquals(tc.min == null, v.isEmpty());
			if(tc.min != null) {
				assertEquals(tc.min, v.get());
			}
		}
	}

	@ParameterizedTest()
	@MethodSource({"dayOfWeek", "localDate"})
	<T extends Comparable<? super T>> void testMissingIntervals(IntervalCollectionTestCase<T> tc) {

		var c = new IntervalCollection<>(tc.cyclic, tc.intervals);
		T min = tc.intervals.stream().map(Interval::getStart).min(Comparator.naturalOrder()).orElse(null);
		T max = tc.intervals.stream().map(Interval::getExclusifEnd).max(Comparator.naturalOrder()).orElse(null);
		
		assertEquals(!tc.missing.isEmpty(), c.isMissingIntervals());
		assertEquals(!tc.missing.isEmpty(), c.isMissingIntervals(min, max));
		
		assertArrayEquals(tc.missing.toArray(), c.missingIntervals().toArray());
		assertArrayEquals(tc.missing.toArray(), c.missingIntervals(min, max).toArray());
		
		assertArrayEquals(tc.missing.toArray(), c.collectMissingIntervals(ImmutableInterval::of, Collectors.toList()).toArray());
		assertArrayEquals(tc.missing.toArray(), c.collectMissingIntervals(min, max, ImmutableInterval::of, Collectors.toList()).toArray());
		
		if(tc.missing.isEmpty()) {
			assertDoesNotThrow(()-> c.requiredNotMissingIntervals());
			assertDoesNotThrow(()-> c.requiredNotMissingIntervals(min, max));
		}
		else {
			Utils.assertException(MissingIntervalException.class, ()-> c.requiredNotMissingIntervals(), e->{
				assertEquals(e.getStart(), tc.missing.get(0).getStart());
				assertEquals(e.getExclusifEnd(), tc.missing.get(0).getExclusifEnd());
			});
			Utils.assertException(MissingIntervalException.class, ()-> c.requiredNotMissingIntervals(min, max), e->{
				assertEquals(e.getStart(), tc.missing.get(0).getStart());
				assertEquals(e.getExclusifEnd(), tc.missing.get(0).getExclusifEnd());
			});
		}
	}

	@ParameterizedTest()
	@MethodSource({"dayOfWeek", "localDate"})
	<T extends Comparable<? super T>> void testOverlapIntervals(IntervalCollectionTestCase<T> tc) {

		var c = new IntervalCollection<>(tc.cyclic, tc.intervals);
		
		assertEquals(!tc.overlap.isEmpty(), c.isOverlapIntervals());
		
		assertArrayEquals(tc.overlap.toArray(), c.overlapIntervals().toArray());
		
		assertArrayEquals(tc.overlap.toArray(), c.collectOverlapIntervals(ImmutableInterval::of, Collectors.toList()).toArray());
		
		if(tc.overlap.isEmpty()) {
			assertDoesNotThrow(()-> c.requiredNotOverlapIntervals());
		}
		else {
			Utils.assertException(OverlapIntervalException.class, ()-> c.requiredNotOverlapIntervals(), e->{
				assertEquals(e.getStart(), tc.overlap.get(0).getStart());
				assertEquals(e.getExclusifEnd(), tc.overlap.get(0).getExclusifEnd());
			});
		}
	}
	

	@ParameterizedTest()
	@MethodSource({"dayOfWeek", "localDate"})
	<T extends Comparable<? super T>> void testDirtyIntervals(IntervalCollectionTestCase<T> tc) {

		var c = new IntervalCollection<>(tc.cyclic, tc.intervals);
		T min = tc.intervals.stream().map(Interval::getStart).min(Comparator.naturalOrder()).orElse(null);
		T max = tc.intervals.stream().map(Interval::getExclusifEnd).max(Comparator.naturalOrder()).orElse(null);
		List<Interval<T>> expectedList = Stream.concat(tc.missing.stream(), tc.overlap.stream()).sorted(Comparator.comparing(Interval::getStart)).collect(Collectors.toList());
		
		assertEquals(expectedList.isEmpty(), c.isLinkedIntervals());
		assertEquals(expectedList.isEmpty(), c.isLinkedIntervals(min, max));
		
		assertArrayEquals(expectedList.toArray(), c.missingAndOverlapIntervals().toArray());
		assertArrayEquals(expectedList.toArray(), c.missingAndOverlapIntervals(min, max).toArray());
		
		assertArrayEquals(expectedList.toArray(), c.collectDirtyIntervals(ImmutableInterval::of, Collectors.toList()).toArray());
		assertArrayEquals(expectedList.toArray(), c.collectDirtyIntervals(min, max, ImmutableInterval::of, Collectors.toList()).toArray());
		
		if(expectedList.isEmpty()) {
			assertDoesNotThrow(()-> c.requiredLinkedIntervals());
			assertDoesNotThrow(()-> c.requiredLinkedIntervals(min, max));
		}
		else {
			if(tc.missingFirst()) {
				Utils.assertException(MissingIntervalException.class, ()-> c.requiredLinkedIntervals(), e->{
					assertEquals(e.getStart(), expectedList.get(0).getStart());
					assertEquals(e.getExclusifEnd(), expectedList.get(0).getExclusifEnd());
				});
				Utils.assertException(MissingIntervalException.class, ()-> c.requiredLinkedIntervals(min, max), e->{
					assertEquals(e.getStart(), expectedList.get(0).getStart());
					assertEquals(e.getExclusifEnd(), expectedList.get(0).getExclusifEnd());
				});
			}
			else {
				Utils.assertException(OverlapIntervalException.class, ()-> c.requiredLinkedIntervals(), e->{
					assertEquals(e.getStart(), expectedList.get(0).getStart());
					assertEquals(e.getExclusifEnd(), expectedList.get(0).getExclusifEnd());
				});
				Utils.assertException(OverlapIntervalException.class, ()-> c.requiredLinkedIntervals(min, max), e->{
					assertEquals(e.getStart(), expectedList.get(0).getStart());
					assertEquals(e.getExclusifEnd(), expectedList.get(0).getExclusifEnd());
				});
			}
		}
	}

}
