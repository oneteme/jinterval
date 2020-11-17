package org.usf.jpartition.partition;

import static org.usf.jpartition.util.CollectionUtils.requiredSameField;
import static org.usf.jpartition.util.CollectionUtils.requiredSameIntField;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.usf.jpartition.core.HasTemporalUnit;
import org.usf.jpartition.core.RegularInterval;
import org.usf.jpartition.core.Serie;
import org.usf.jpartition.partition.MultiModelPart.SubItem;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Partitions {
	
	private static final SubItem<Object, Object> IDENTITY = (o, i)-> o;
	
	public static <P, T extends Temporal & Comparable<? super T>, S extends Serie<T, ? extends P>> MultiModelPartition<T, P> ofSeries(List<S> series) {

		return ofPeriods(series, requiredSameIntField(series, Serie::getStep), Serie::getPoint, null, null);
	}
	
	public static <P, T extends Temporal & Comparable<? super T>, S extends Serie<T, ? extends P>> MultiModelPartition<T, P> ofSeries(List<S> series, T start, T exclusifEnd) {
		
		return ofPeriods(series, requiredSameIntField(series, Serie::getStep), Serie::getPoint, start, exclusifEnd);
	}
	
	public static <U, T extends Temporal & Comparable<? super T>, P extends RegularInterval<T> & HasTemporalUnit> MultiModelPartition<T, U> ofPeriods(List<P> periods, int step, SubItem<P, U> fn) {
		
		return ofPeriods(periods, step, fn, null, null);
	}
	
	public static <U, T extends Temporal & Comparable<? super T>, P extends RegularInterval<T> & HasTemporalUnit> MultiModelPartition<T, U> 
		ofPeriods(List<P> periods, int step, SubItem<P, U> fn, T start, T exclusifEnd) {
		
		ChronoUnit unit = requiredSameField(periods, HasTemporalUnit::getTemporalUnit);
		return ofIntervals(periods, start, exclusifEnd, (min, sp)-> until(min, sp, unit, step), fn);
	}
	

	public static <T extends Temporal & Comparable<? super T>, P extends RegularInterval<T>> MultiModelPartition<T, P> ofIntervals(Collection<P> periods) {
		return ofIntervals(periods, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Temporal & Comparable<? super T>, P extends RegularInterval<T>> MultiModelPartition<T, P> ofIntervals(Collection<P> periods, T start, T exclusifEnd) {
		
		AtomicInteger cp = new AtomicInteger();
		return ofIntervals(periods, start, exclusifEnd, (min, sp)-> cp.incrementAndGet(), (SubItem<P, P>) IDENTITY);
	}
	
	private static <U, T extends Comparable<? super T>, P extends RegularInterval<T>> MultiModelPartition<T, U> 
		ofIntervals(Collection<P> intervals, T start, T exclusifEnd, ToIntBiFunction<T, T> indexFn, SubItem<P, U> subFn) {
		
		List<P> list = toList(intervals);
		List<MultiModelPart<T,?,U>> partitions = new LinkedList<>();
		intervalParts(list, start, exclusifEnd, (sp, ep, indexs, min)-> 
			partitions.add(new MultiModelPart<>(list, indexs, indexFn.applyAsInt(min, sp), indexFn.applyAsInt(min, ep), start, exclusifEnd, subFn)));
		return new MultiModelPartition<>(partitions);
	}

	public static <T extends Comparable<? super T>> void intervalParts(List<? extends RegularInterval<T>> intervals, PartConsumer<T> consumer) {
		
		intervalParts(intervals, null, null, consumer);
	}
	public static <T extends Comparable<? super T>> void intervalParts(List<? extends RegularInterval<T>> intervals, T start, T exclusifEnd, PartConsumer<T> consumer) {
		
		Set<T> marks = new HashSet<>(intervals.size() * 2);
		intervals.forEach(i->{
			marks.add(i.getStart());
			marks.add(i.getExclusifEnd());
		});
		if(start != null) {
			marks.add(start);
		}
		if(exclusifEnd != null) {
			marks.add(exclusifEnd);
		}
		List<T> ordredMarks = marks.stream().sorted().collect(Collectors.toList());
		for(int i=1; i<ordredMarks.size(); i++) {
			T pre = ordredMarks.get(i-1), cur = ordredMarks.get(i);
			consumer.accept(pre, cur, IntStream.range(0, intervals.size())
					.filter(it-> intervals.get(it).intersectInterval(pre, cur))
					.toArray(), ordredMarks.get(0));
		}
	}
	
	private static final <T> List<T> toList(Collection<T> c){
		return c instanceof List ? (List<T>) c : new ArrayList<>(c);
	}
	
	public interface PartConsumer<T> {
		
		void accept(T start, T exclusifEnd, int[] indexs, T min);
	}
	
	private static int until(Temporal t1, Temporal t2, ChronoUnit unit, int step) {

		return (int)(t1.until(t2, unit) / step);
	}
}
