package org.usf.jinterval.core;

import static org.usf.jinterval.partition.Partitions.intervalParts;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.usf.jinterval.core.exception.MissingIntervalException;
import org.usf.jinterval.core.exception.OverlapIntervalException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntervalCollection<T extends Comparable<? super T>> {
	
	private static final IntPredicate MISSING_INTERVALS_FILTER = i-> i == 0;
	private static final IntPredicate OVERLAP_INTERVALS_FILTER = i-> i > 1;
	private static final IntPredicate DIRTY_INTERVALS_FILTER   = i-> i != 1;
	
	private final boolean cyclic;
	private final List<? extends Interval<T>> intervals;
	
	public Optional<Interval<T>> minInterval() {
		if(cyclic) {
			throw new UnsupportedOperationException();
		}
		return intervals.stream().collect(IntervalCollector.minInterval());
	}

	public Optional<Interval<T>> maxInterval() {
		if(cyclic) {
			throw new UnsupportedOperationException();
		}
		return intervals.stream().collect(IntervalCollector.maxInterval());
	}
	
	public boolean isMissingIntervals() {

		return testIf(null, null, MISSING_INTERVALS_FILTER);
	}
	public boolean isMissingIntervals(T start, T exclusifEnd) {

		return testIf(start, exclusifEnd, MISSING_INTERVALS_FILTER);
	}

	public void requiredNotMissingIntervals() {
	
		throwsIf(null, null, MISSING_INTERVALS_FILTER);
	}
	public void requiredNotMissingIntervals(T start, T exclusifEnd) {

		throwsIf(start, exclusifEnd, MISSING_INTERVALS_FILTER);
	}
	
	public boolean isOverlapIntervals() {

		return testIf(null, null, OVERLAP_INTERVALS_FILTER);
	}

	public void requiredNotOverlapIntervals() {

		throwsIf(null, null, OVERLAP_INTERVALS_FILTER);
	}
	
	public boolean isLinkedIntervals() {
		
		return !testIf(null, null, DIRTY_INTERVALS_FILTER);
	}
	public boolean isLinkedIntervals(T start, T exclusifEnd) {
		
		return !testIf(start, exclusifEnd, DIRTY_INTERVALS_FILTER);
	}

	public void requiredLinkedIntervals() {

		throwsIf(null, null, DIRTY_INTERVALS_FILTER);
	}

	public void requiredLinkedIntervals(T start, T exclusifEnd) {

		throwsIf(start, exclusifEnd, DIRTY_INTERVALS_FILTER);
	}


	public List<Interval<T>> missingIntervals() {

		return filter(null, null, MISSING_INTERVALS_FILTER, ImmutableInterval::of, Collectors.toList());
	}
	public List<Interval<T>> missingIntervals(T start, T exclusifEnd) {
		
		return filter(start, exclusifEnd, MISSING_INTERVALS_FILTER, ImmutableInterval::of, Collectors.toList());
	}
	
	public List<Interval<T>> overlapIntervals() {

		return filter(null, null, OVERLAP_INTERVALS_FILTER, ImmutableInterval::of, Collectors.toList());
	}
	
	public List<Interval<T>> missingAndOverlapIntervals() {

		return filter(null, null, DIRTY_INTERVALS_FILTER, ImmutableInterval::of, Collectors.toList());
	}
	public List<Interval<T>> missingAndOverlapIntervals(T start, T exclusifEnd) {

		return filter(start, exclusifEnd, DIRTY_INTERVALS_FILTER, ImmutableInterval::of, Collectors.toList());
	}
	
	public <I extends Interval<T>, R> R collectMissingIntervals(BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(null, null, MISSING_INTERVALS_FILTER, fn, collector);
	}
	public <I extends Interval<T>, R> R collectMissingIntervals(T start, T exclusifEnd, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {
		
		return filter(start, exclusifEnd, MISSING_INTERVALS_FILTER, fn, collector);
	}
	
	public <I extends Interval<T>, R> R collectOverlapIntervals(BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(null, null, OVERLAP_INTERVALS_FILTER, fn, collector);
	}

	public <I extends Interval<T>, R> R collectDirtyIntervals(BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(null, null, DIRTY_INTERVALS_FILTER, fn, collector);
	}
	public <I extends Interval<T>, R> R collectDirtyIntervals(T start, T exclusifEnd, BiFunction<T, T, I> fn, Collector<I, ?, R> collector) {

		return filter(start, exclusifEnd, DIRTY_INTERVALS_FILTER, fn, collector);
	}

	public <I extends Interval<T>, C, R> R filter(T start, T exclusifEnd, IntPredicate intervalCount, BiFunction<T, T, I> fn, Collector<I, C, R> collector) {

		C list = collector.supplier().get();
		acceptIf(start, exclusifEnd, intervalCount, (sp, ep)-> collector.accumulator().accept(list, fn.apply(sp, ep)));
		return collector.finisher().apply(list);
	}
	
	public void acceptIf(T start, T exclusifEnd, IntPredicate nIntervalPredicate, BiConsumer<T,T> consumer) {

		intervalParts(cyclic, intervals, start, exclusifEnd, (sp, ep, indexs, min)->{
			if(nIntervalPredicate.test(indexs.length)) {
				consumer.accept(sp, ep);
			}
		});
	}
	
	private boolean testIf(T start, T exclusifEnd, IntPredicate nIntervalPredicate) {
		
		try {
			throwsIf(start, exclusifEnd, nIntervalPredicate);
		} catch (Exception e) {
			return true;
		}
		return false;
	}
	
	private void throwsIf(T start, T exclusifEnd, IntPredicate nIntervalPredicate) {

		intervalParts(cyclic, intervals, start, exclusifEnd, (sp, ep, indexs, min)->{
			if(nIntervalPredicate.test(indexs.length)) {
				throw indexs.length == 0 
						? new MissingIntervalException(sp, ep) 
						: new OverlapIntervalException(sp, ep);
			}
		});
	}
	
}
