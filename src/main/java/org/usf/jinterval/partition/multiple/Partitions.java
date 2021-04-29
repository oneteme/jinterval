package org.usf.jinterval.partition.multiple;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.usf.jinterval.core.IntervalUtils.requiredPositifDirection;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.usf.jinterval.core.Interval;
import org.usf.jinterval.core.IntervalUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Partitions {
		
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, int step, ChronoUnit unit) {
		
		return periodPartition(periods, null, null, step, unit);
	}
	
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, T start, T exclusifEnd, int step, ChronoUnit unit) {
		
		requiredPositifDirection(start, exclusifEnd);
		requiredPositifDirection(periods);
		ToIntBiFunction<T, T> indexFn = (min, tp)-> (int)(unit.between(min, tp) / step);
		return intervalPartition(periods, start, exclusifEnd, indexFn, indexFn);
	}
	

	public static <T extends Comparable<? super T>, P extends Interval<T>> MultiModelPartition<P> intervalPartition(Collection<P> intervals) {
		
		return intervalPartition(intervals, null, null);
	}
	
	public static <T extends Comparable<? super T>, P extends Interval<T>> MultiModelPartition<P> intervalPartition(Collection<P> intervals, T start, T exclusifEnd) {
		
		AtomicInteger cp = new AtomicInteger();
		return intervalPartition(intervals, start, exclusifEnd, (min, sp)-> cp.get(), (min, sp)-> cp.incrementAndGet());
	}
	
	private static <T extends Comparable<? super T>, P extends Interval<T>> MultiModelPartition<P> intervalPartition(Collection<P> intervals, T start, T exclusifEnd, 
			ToIntBiFunction<T, T> startIndexFn, ToIntBiFunction<T, T> endIndexFn) {
		
		List<MultiModelPart<P>> partitions = new LinkedList<>();
		intervalParts(intervals, start, exclusifEnd, 
				(sp, ep, model, min)-> partitions.add(
						new MultiModelPart<>(model.collect(toList()), startIndexFn.applyAsInt(min, sp), endIndexFn.applyAsInt(min, ep), sp, ep)));
		
		return new MultiModelPartition<>(partitions);
	}

	public static <T extends Comparable<? super T>, I extends Interval<T>> void intervalParts(Collection<I> intervals, PartConsumer<T, I> consumer) {
		
		intervalParts(intervals, null, null, consumer);
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> void intervalParts(Collection<I> intervals, T start, T exclusifEnd, PartConsumer<T, I> consumer) {
		
		Set<T> marks = new HashSet<>(intervals.size() * 2);
		boolean regular = true;
		for(var i : intervals){
			marks.add(i.getStart());
			marks.add(i.getExclusifEnd());
			regular &= i.isRegular();
		}
		if(start != null && exclusifEnd != null) {
			if(IntervalUtils.direction(start, exclusifEnd) < 1) {
				regular = false;
			}
			else if(!regular) {
				throw new IllegalArgumentException(IntervalUtils.toString(start, exclusifEnd) + " can't be regular");
			}
		}
		if(start != null) {
			marks.add(start);
		}
		if(exclusifEnd != null) {
			marks.add(exclusifEnd);
		}
		List<T> ordredMarks = marks.stream().sorted().collect(Collectors.toList());
		if(!regular) {
			ordredMarks.add(ordredMarks.get(0));
		}
		for(int i=1; i<ordredMarks.size(); i++) {
			T pre = ordredMarks.get(i-1);
			T cur = ordredMarks.get(i);
			consumer.accept(pre, cur, intervals.stream()
					.filter(m-> m.intersectInterval(pre, cur)), ordredMarks.get(0));
		}
	}

	public interface PartConsumer<T,M> {
		
		void accept(T start, T exclusifEnd, Stream<M> models, T min);
	}

}
