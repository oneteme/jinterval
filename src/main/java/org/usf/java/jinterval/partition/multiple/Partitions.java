package org.usf.java.jinterval.partition.multiple;

import static java.util.Collections.rotate;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.usf.java.jinterval.core.IntervalUtils.direction;
import static org.usf.java.jinterval.core.IntervalUtils.requiredPositifDirection;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.usf.java.jinterval.core.ImmutableInterval;
import org.usf.java.jinterval.core.Interval;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Partitions {
	
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, int step, ChronoUnit unit, Function<T, ? extends Temporal> fn) {
		
		return periodPartition(periods, null, null, step, unit, fn);
	}	
	
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, T start, T exclusifEnd, int step, ChronoUnit unit, Function<T, ? extends Temporal> fn) {
		
		ToIntBiFunction<T, T> indexFn = (min, tp)-> (int)(unit.between(fn.apply(min), fn.apply(tp)) / step); //TODO convert 
		return intervalPartition(false, periods, start, exclusifEnd, indexFn, indexFn);
	}

	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, int step, ChronoUnit unit) {
		
		return periodPartition(periods, null, null, step, unit);
	}
	
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, T start, T exclusifEnd, int step, ChronoUnit unit) {
		
		ToIntBiFunction<T, T> indexFn = (min, tp)-> (int)(unit.between(min, tp) / step);
		return intervalPartition(false, periods, start, exclusifEnd, indexFn, indexFn);
	}
	public static <T extends Comparable<? super T>, P extends Interval<T>> MultiModelPartition<P> intervalPartition(boolean inverted, Collection<P> intervals) {
		
		return intervalPartition(inverted, intervals, null, null);
	}
	
	public static <T extends Comparable<? super T>, P extends Interval<T>> MultiModelPartition<P> intervalPartition(boolean inverted, Collection<P> intervals, T start, T exclusifEnd) {
		
		var cp = new AtomicInteger();
		return intervalPartition(inverted, intervals, start, exclusifEnd, (min, sp)-> cp.get(), (min, sp)-> cp.incrementAndGet());
	}
	
	private static <T extends Comparable<? super T>, P extends Interval<T>> MultiModelPartition<P> intervalPartition(boolean inverted, Collection<P> intervals, T start, T exclusifEnd, 
			ToIntBiFunction<T, T> startIndexFn, ToIntBiFunction<T, T> endIndexFn) {
		
		List<MultiModelPart<P>> partitions = new LinkedList<>();
		intervalParts(inverted, intervals, start, exclusifEnd, 
				(sp, ep, model, min)-> partitions.add(
						new MultiModelPart<>(model.collect(toList()), startIndexFn.applyAsInt(min, sp), endIndexFn.applyAsInt(min, ep), sp, ep)));
		
		return new MultiModelPartition<>(partitions);
	}

	public static <T extends Comparable<? super T>, I extends Interval<T>> void intervalParts(boolean inverted, Collection<I> intervals, PartConsumer<T, I> consumer) {
		
		intervalParts(inverted, intervals, null, null, consumer);
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> void intervalParts(boolean inverted, Collection<I> intervals, T startInclusive, T endExclusive, PartConsumer<T, I> consumer) {
		
		if(!inverted) {
			requiredPositifDirection(intervals);
			if(startInclusive != null && endExclusive != null) {
				requiredPositifDirection(startInclusive, endExclusive);
			}
		}
		Set<T> marks = new HashSet<>(intervals.size() * 2);
		Predicate<T> filter = o->true;
		if(startInclusive != null && endExclusive != null) {
			filter = new ImmutableInterval<>(startInclusive, endExclusive)::containsField;
			marks.add(startInclusive);
			marks.add(endExclusive);
		}
		else if(startInclusive != null) {
			filter = o-> startInclusive.compareTo(o) <= 0;
			marks.add(startInclusive);
		}
		else if(endExclusive != null) {
			filter = o-> endExclusive.compareTo(o) > 0;
			marks.add(endExclusive);
		}
		for(var o : intervals){
			if(filter.test(o.startInclusive())) {
				marks.add(o.startInclusive());
			}
			if(filter.test(o.endExclusive())) {
				marks.add(o.endExclusive());
			}
		}
		if(!marks.isEmpty()) {
			List<T> ordredMarks = marks.stream().sorted().collect(Collectors.toList());
			if(inverted) {
				if(startInclusive != null && endExclusive != null && direction(startInclusive, endExclusive)<=0) {
					rotate(ordredMarks, -ordredMarks.indexOf(startInclusive));
				}
				if(Objects.equals(startInclusive, endExclusive)) {
					ordredMarks.add(ordredMarks.get(0));
				}
			}
			ordredMarks.stream().reduce((pre, cur)->{
				consumer.accept(pre, cur, intervals.stream().filter(m-> m.intersectInterval(pre, cur)), ordredMarks.get(0));
				return cur;
			});
		}
	}
	
	public interface PartConsumer<T,M> {
		
		void accept(T start, T exclusifEnd, Stream<M> models, T min);
	}
	
}
