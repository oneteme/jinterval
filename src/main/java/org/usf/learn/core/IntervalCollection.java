package org.usf.learn.core;

import static org.usf.learn.core.partition.Partitions.intervalParts;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.usf.learn.exception.MissingIntervalException;
import org.usf.learn.exception.OverlapIntervalException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntervalCollection<T extends Comparable<? super T>> {
	
	private static final IntPredicate MISSING_INTERVALS_FILTER = i-> i == 0;
	private static final IntPredicate OVERLAP_INTERVALS_FILTER = i-> i > 1;
	private static final IntPredicate UNLINKED_INTERVALS_FILTER = i-> i != 1;
	
	private final List<? extends Interval<T>> intervals;
	
	public boolean isMissingIntervals() {
		
		return isMissingIntervals(null, null);
	}
	public boolean isMissingIntervals(T start, T exclusifEnd) {
		try {
			requiredNotMissingIntervals	(start, exclusifEnd);
		} catch (MissingIntervalException e) {
			return true;
		}
		return false;
	}

	public void requiredNotMissingIntervals() {
	
		requiredNotMissingIntervals(null, null);
	}
	public void requiredNotMissingIntervals(T start, T exclusifEnd) {

		throwsIf(start, exclusifEnd, MISSING_INTERVALS_FILTER);
	}
	
	public boolean isOverlapIntervals() {
		
		return isOverlapIntervals(null, null);
	}
	public boolean isOverlapIntervals(T start, T exclusifEnd) {
		try {
			requiredNotOverlapIntervals(start, exclusifEnd);
		} catch (OverlapIntervalException e) {
			return true;
		}
		return false;
	}

	public void requiredNotOverlapIntervals() {

		acceptIf(null, null, OVERLAP_INTERVALS_FILTER , OverlapIntervalException::new);
	}
	public void requiredNotOverlapIntervals(T start, T exclusifEnd) {

		throwsIf(start, exclusifEnd, OVERLAP_INTERVALS_FILTER);
	}
	
	public boolean isLinkedIntervals() {
		
		return isLinkedIntervals(null, null);
	}
	public boolean isLinkedIntervals(T start, T exclusifEnd) {
		try {
			requiredLinkedIntervals	(start, exclusifEnd);
		} catch (MissingIntervalException | OverlapIntervalException e) {
			return false;
		}
		return true;
	}

	public void requiredLinkedIntervals() {
	
		requiredLinkedIntervals(null, null);
	}
	public void requiredLinkedIntervals(T start, T exclusifEnd) {

		throwsIf(start, exclusifEnd, UNLINKED_INTERVALS_FILTER);
	}

	public List<Interval<T>> missingIntervals() {

		return filter(null, null, MISSING_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	public List<Interval<T>> missingIntervals(T start, T exclusifEnd) {
		return filter(start, exclusifEnd, MISSING_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	public <I extends Interval<T>, R> R missingIntervals(BiFunction<T, T, I> fn, Collector<I, Collection<I>, R> collector) {

		return filter(null, null, MISSING_INTERVALS_FILTER, fn, collector);
	}
	public <I extends Interval<T>, R> R missingIntervals(T start, T exclusifEnd, BiFunction<T, T, I> fn, Collector<I, Collection<I>, R> collector) {
		
		return filter(start, exclusifEnd, MISSING_INTERVALS_FILTER, fn, collector);
	}
	
	public List<Interval<T>> overlapIntervals() {

		return filter(null, null, OVERLAP_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	public List<Interval<T>> overlapIntervals(T start, T exclusifEnd) {
		return filter(start, exclusifEnd, OVERLAP_INTERVALS_FILTER, ImmutableInterval::new, Collectors.toList());
	}
	
	public <I extends Interval<T>, R> R overlapIntervals(BiFunction<T, T, I> fn, Collector<I, Collection<I>, R> collector) {

		return filter(null, null, OVERLAP_INTERVALS_FILTER, fn, collector);
	}
	public <I extends Interval<T>, R> R overlapIntervals(T start, T exclusifEnd, BiFunction<T, T, I> fn, Collector<I, Collection<I>, R> collector) {
		
		return filter(start, exclusifEnd, OVERLAP_INTERVALS_FILTER, fn, collector);
	}

	public <I extends Interval<T>, C, R> R filter(T start, T exclusifEnd, IntPredicate intervalCount, BiFunction<T, T, I> fn, Collector<I, C, R> collector) {

		C list = collector.supplier().get();
		acceptIf(start, exclusifEnd, intervalCount, (sp, ep)-> collector.accumulator().accept(list, fn.apply(sp, ep)));
		return collector.finisher().apply(list);
	}
	
	public void acceptIf(T start, T exclusifEnd, IntPredicate nIntervalPredicate, BiConsumer<T,T> consumer) {

		intervalParts(intervals, start, exclusifEnd, (sp, ep, indexs, min)->{
			if(nIntervalPredicate.test(indexs.length)) {
				consumer.accept(sp, ep);
			}
		});
	}
	
	public void throwsIf(T start, T exclusifEnd, IntPredicate nIntervalPredicate) {

		intervalParts(intervals, start, exclusifEnd, (sp, ep, indexs, min)->{
			if(nIntervalPredicate.test(indexs.length)) {
				throw indexs.length == 0 
						? new MissingIntervalException(sp, ep) 
						: new OverlapIntervalException(sp, ep);
			}
		});
	}

}
