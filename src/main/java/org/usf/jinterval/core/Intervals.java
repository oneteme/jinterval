package org.usf.jinterval.core;

import static org.usf.jinterval.core.IntervalCollector.largestInterval;
import static org.usf.jinterval.core.IntervalCollector.smallestInterval;
import static org.usf.jinterval.partition.multiple.Partitions.intervalParts;

import java.util.Collection;
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
	
	public static <T extends Comparable<? super T>> Optional<Interval<T>> minInterval(Collection<? extends Interval<T>> intervals) {

		return intervals.stream().collect(smallestInterval());
	}

	public static <T extends Comparable<? super T>> Optional<Interval<T>> maxInterval(Collection<? extends Interval<T>> intervals) {

		return intervals.stream().collect(largestInterval());
	}
	
	/******** testIf ********/
	
	public static <T extends Comparable<? super T>> boolean isMissingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		return testIf(reversed, null, null, intervals, MISSING_INTERVALS_FILTER);
	}
	public static <T extends Comparable<? super T>> boolean isMissingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		return testIf(reversed, start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER);
	}

	public  static <T extends Comparable<? super T>> boolean isOverlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		return testIf(reversed, null, null, intervals, OVERLAP_INTERVALS_FILTER);
	}
	public  static <T extends Comparable<? super T>> boolean isOverlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		return testIf(reversed, start, exclusifEnd, intervals, OVERLAP_INTERVALS_FILTER);
	}
	
	public  static <T extends Comparable<? super T>> boolean isMissingOrOverlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		return testIf(reversed, null, null, intervals, DIRTY_INTERVALS_FILTER);
	}
	public  static <T extends Comparable<? super T>> boolean isMissingOrOverlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		return testIf(reversed, start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER);
	}

	/******** throwsIf ********/

	public static <T extends Comparable<? super T>> void requiredNotMissingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {
	
		throwsIf(reversed, null, null, intervals, MISSING_INTERVALS_FILTER, "missing interval");
	}
	public static <T extends Comparable<? super T>> void requiredNotMissingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		throwsIf(reversed, start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER, "missing interval");
	}

	public  static <T extends Comparable<? super T>> void requiredNotOverlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		throwsIf(reversed, null, null, intervals, OVERLAP_INTERVALS_FILTER, "overlaping interval");
	}
	public  static <T extends Comparable<? super T>> void requiredNotOverlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		throwsIf(reversed, start, exclusifEnd, intervals, OVERLAP_INTERVALS_FILTER, "overlaping interval");
	}
	
	public  static <T extends Comparable<? super T>> void requiredNotMissingAndOverlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		throwsIf(reversed, null, null, intervals, DIRTY_INTERVALS_FILTER, "mismatch interval");
	}
	public  static <T extends Comparable<? super T>> void requiredNotMissingAndOverlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		throwsIf(reversed, start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER, "mismatch interval");
	}

	/******** filter ********/

	public static <T extends Comparable<? super T>> List<Interval<T>> missingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		return filter(reversed, null, null, intervals, MISSING_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	public static <T extends Comparable<? super T>> List<Interval<T>> missingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {
		
		return filter(reversed, start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	public static <T extends Comparable<? super T>> List<Interval<T>> overlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		return filter(reversed, null, null, intervals, OVERLAP_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	public static <T extends Comparable<? super T>> List<Interval<T>> overlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		return filter(reversed, start, exclusifEnd, intervals, OVERLAP_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	public static <T extends Comparable<? super T>> List<Interval<T>> missingAndOverlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals) {

		return filter(reversed, null, null, intervals, DIRTY_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	public static <T extends Comparable<? super T>> List<Interval<T>> missingAndOverlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals) {

		return filter(reversed, start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	/******** collect ********/
	
	public static <T extends Comparable<? super T>, I, R> R collectMissingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(reversed, null, null, intervals, MISSING_INTERVALS_FILTER, fn, collector);
	}
	public static <T extends Comparable<? super T>, I, R> R collectMissingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {
		
		return filter(reversed, start, exclusifEnd, intervals, MISSING_INTERVALS_FILTER, fn, collector);
	}
	
	public static <T extends Comparable<? super T>, I, R> R collectOverlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(reversed, null, null, intervals, OVERLAP_INTERVALS_FILTER, fn, collector);
	}
	public static <T extends Comparable<? super T>, I, R> R collectOverlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(reversed, start, exclusifEnd, intervals, OVERLAP_INTERVALS_FILTER, fn, collector);
	}

	public static <T extends Comparable<? super T>, I, R> R collectMissingAndOverlappingIntervals(boolean reversed, Collection<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(reversed, null, null, intervals, DIRTY_INTERVALS_FILTER, fn, collector);
	}
	public static <T extends Comparable<? super T>, I, R> R collectMissingAndOverlappingIntervals(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(reversed, start, exclusifEnd, intervals, DIRTY_INTERVALS_FILTER, fn, collector);
	}

	public static <T extends Comparable<? super T>, I, C, R> R filter(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals, IntPredicate intervalCount, BiFunction<T, T, I> fn, Collector<I, C, R> collector) {

		C list = collector.supplier().get();
		acceptIf(reversed, start, exclusifEnd, intervals, intervalCount, 
				(sp, ep)-> collector.accumulator().accept(list, fn.apply(sp, ep)));
		return collector.finisher().apply(list);
	}
	
	private static <T extends Comparable<? super T>> boolean testIf(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals, IntPredicate nIntervalPredicate) {
		
		try {
			throwsIf(reversed, start, exclusifEnd, intervals, nIntervalPredicate, null);
		} catch (IntervalMismatchException e) {
			return true;
		}
		return false;
	}
	
	private static <T extends Comparable<? super T>> void throwsIf(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals, IntPredicate nIntervalPredicate, String message) {

		acceptIf(reversed, start, exclusifEnd, intervals, nIntervalPredicate, 
				(s,e)-> {throw new IntervalMismatchException(message);});
	}

	public static <T extends Comparable<? super T>> void acceptIf(boolean reversed, T start, T exclusifEnd, Collection<? extends Interval<T>> intervals, IntPredicate nIntervalPredicate, BiConsumer<T,T> consumer) {

		intervalParts(reversed, intervals, start, exclusifEnd, (sp, ep, models, min)->{
			if(nIntervalPredicate.test((int)models.count())) {
				consumer.accept(sp, ep);
			}
		});
	}
	
}
