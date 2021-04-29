package org.usf.jinterval.core;

import static org.usf.jinterval.core.IntervalCollector.largestInterval;
import static org.usf.jinterval.core.IntervalCollector.smallestInterval;
import static org.usf.jinterval.partition.multiple.Partitions.intervalParts;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.usf.jinterval.core.exception.IntervalMismatchException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Intervals {
	
	private static final IntPredicate MISSING_INTERVALS_FILTER = i-> i == 0;
	private static final IntPredicate OVERLAP_INTERVALS_FILTER = i-> i > 1;
	private static final IntPredicate DIRTY_INTERVALS_FILTER   = i-> i != 1;
	
	
	public static <T extends Comparable<? super T>> Optional<Interval<T>> minInterval(List<? extends Interval<T>> intervals) {

		return intervals.stream().collect(smallestInterval());
	}

	public static <T extends Comparable<? super T>> Optional<Interval<T>> maxInterval(List<? extends Interval<T>> intervals) {

		return intervals.stream().collect(largestInterval());
	}
	
	public static <T extends Comparable<? super T>> boolean isMissingIntervals(List<? extends Interval<T>> intervals) {

		return testIf(null, null, intervals, MISSING_INTERVALS_FILTER);
	}
	public static <T extends Comparable<? super T>> boolean isMissingIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals) {

		return testIf(start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER);
	}

	public static <T extends Comparable<? super T>> void requiredNotMissingIntervals(List<? extends Interval<T>> intervals) {
	
		throwsIf(null, null, intervals, MISSING_INTERVALS_FILTER, "missing interval");
	}
	public static <T extends Comparable<? super T>> void requiredNotMissingIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals) {

		throwsIf(start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER, "missing interval");
	}
	
	public  static <T extends Comparable<? super T>> boolean isOverlapIntervals(List<? extends Interval<T>> intervals) {

		return testIf(null, null, intervals, OVERLAP_INTERVALS_FILTER);
	}

	public  static <T extends Comparable<? super T>> void requiredNotOverlapIntervals(List<? extends Interval<T>> intervals) {

		throwsIf(null, null, intervals, OVERLAP_INTERVALS_FILTER, "overlaping interval");
	}
	
	public  static <T extends Comparable<? super T>> boolean isLinkedIntervals(List<? extends Interval<T>> intervals) {
		
		return !testIf(null, null, intervals, DIRTY_INTERVALS_FILTER);
	}
	public  static <T extends Comparable<? super T>> boolean isLinkedIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals) {
		
		return !testIf(start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER);
	}

	public static <T extends Comparable<? super T>> void requiredLinkedIntervals(List<? extends Interval<T>> intervals) {

		throwsIf(null, null, intervals, DIRTY_INTERVALS_FILTER, "mismatch interval");
	}

	public static <T extends Comparable<? super T>> void requiredLinkedIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals) {

		throwsIf(start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER, "mismatch interval");
	}


	public static <T extends Comparable<? super T>> List<Interval<T>> missingIntervals(List<? extends Interval<T>> intervals) {

		return filter(null, null, intervals, MISSING_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	public static <T extends Comparable<? super T>> List<Interval<T>> missingIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals) {
		
		return filter(start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	public static <T extends Comparable<? super T>> List<Interval<T>> overlapIntervals(List<? extends Interval<T>> intervals) {

		return filter(null, null, intervals, OVERLAP_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	public static <T extends Comparable<? super T>> List<Interval<T>> missingAndOverlapIntervals(List<? extends Interval<T>> intervals) {

		return filter(null, null, intervals, DIRTY_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	public static <T extends Comparable<? super T>> List<Interval<T>> missingAndOverlapIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals) {

		return filter(start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>, R> R collectMissingIntervals(List<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(null, null, intervals, MISSING_INTERVALS_FILTER, fn, collector);
	}
	public static <T extends Comparable<? super T>, I extends Interval<T>, R> R collectMissingIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {
		
		return filter(start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER, fn, collector);
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>, R> R collectOverlapIntervals(List<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(null, null, intervals, OVERLAP_INTERVALS_FILTER, fn, collector);
	}

	public static <T extends Comparable<? super T>, I extends Interval<T>, R> R collectDirtyIntervals(List<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(null, null, intervals, DIRTY_INTERVALS_FILTER, fn, collector);
	}
	public static <T extends Comparable<? super T>, I extends Interval<T>, R> R collectDirtyIntervals(T start, T exclusifEnd, List<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER, fn, collector);
	}

	public static <T extends Comparable<? super T>, I extends Interval<T>, C, R> R filter(T start, T exclusifEnd, List<? extends Interval<T>> intervals, IntPredicate intervalCount, BiFunction<T, T, I> fn, Collector<I, C, R> collector) {

		C list = collector.supplier().get();
		acceptIf(start, exclusifEnd, intervals, intervalCount, 
				(sp, ep)-> collector.accumulator().accept(list, fn.apply(sp, ep)));
		return collector.finisher().apply(list);
	}
	
	private static <T extends Comparable<? super T>> boolean testIf(T start, T exclusifEnd, List<? extends Interval<T>> intervals, IntPredicate nIntervalPredicate) {
		
		try {
			throwsIf(start, exclusifEnd, intervals, nIntervalPredicate, null);
		} catch (IntervalMismatchException e) {
			return true;
		}
		return false;
	}
	
	private static <T extends Comparable<? super T>> void throwsIf(T start, T exclusifEnd, List<? extends Interval<T>> intervals, IntPredicate nIntervalPredicate, String message) {

		acceptIf(start, exclusifEnd, intervals, nIntervalPredicate, 
				(s,e)-> {throw new IntervalMismatchException(message);});
	}

	public static <T extends Comparable<? super T>> void acceptIf(T start, T exclusifEnd, List<? extends Interval<T>> intervals, IntPredicate nIntervalPredicate, BiConsumer<T,T> consumer) {

		intervalParts(intervals, start, exclusifEnd, (sp, ep, models, min)->{
			if(nIntervalPredicate.test((int)models.count())) {
				consumer.accept(sp, ep);
			}
		});
	}
	
	
}
