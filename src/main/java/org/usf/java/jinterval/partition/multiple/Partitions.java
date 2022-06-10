package org.usf.java.jinterval.partition.multiple;

import static java.util.Collections.rotate;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.usf.java.jinterval.core.IntervalUtils.direction;
import static org.usf.java.jinterval.core.IntervalUtils.requiredPositifDirection;
import static org.usf.java.jinterval.core.TemporalUtils.nStepBetween;
import static org.usf.java.jinterval.partition.multiple.Partitions.IndexPartConsumer.appender;

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

		List<MultiModelPart<P>> parts = new LinkedList<>();
		intervalParts(false, periods, start, exclusifEnd, appender(parts).toPartConsumer(step, unit, fn));
		return new MultiModelPartition<>(parts);
	}

	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, int step, ChronoUnit unit) {
		
		return periodPartition(periods, null, null, step, unit);
	}
	
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> MultiModelPartition<P> periodPartition(Collection<P> periods, T start, T exclusifEnd, int step, ChronoUnit unit) {

		List<MultiModelPart<P>> parts = new LinkedList<>();
		intervalParts(false, periods, start, exclusifEnd, appender(parts).toPartConsumer(step, unit));
		return new MultiModelPartition<>(parts);
	}
	
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> void periodPartition(Collection<P> periods, int step, ChronoUnit unit, IndexPartConsumer<T, P> fn) {
		
		periodPartition(periods, null, null, step, unit, fn);
	}
	
	public static <T extends Comparable<? super T> & Temporal, P extends Interval<T>> void periodPartition(Collection<P> periods, T start, T exclusifEnd, int step, ChronoUnit unit, IndexPartConsumer<T, P> fn) {

		intervalParts(false, periods, start, exclusifEnd, fn.toPartConsumer(step, unit));
	}
	
	public static <T extends Comparable<? super T>, P extends Interval<T>> MultiModelPartition<P> intervalPartition(boolean inverted, Collection<P> intervals) {
		
		return intervalPartition(inverted, intervals, null, null);
	}
		
	public static <T extends Comparable<? super T>, I extends Interval<T>> MultiModelPartition<I> intervalPartition(boolean inverted, Collection<I> intervals, T start, T exclusifEnd) {

		List<MultiModelPart<I>> parts = new LinkedList<>();
		intervalParts(inverted, intervals, start, exclusifEnd, appender(parts).toPartConsumer());
		return new MultiModelPartition<>(parts);
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
	
	@FunctionalInterface
	public interface PartConsumer<T,M> {
		
		void accept(T startInclusive, T endExclusive, Stream<M> models, T min);
	}	

	@FunctionalInterface
	public interface IndexPartConsumer<T,M> {
		
		void accept(T startInclusive, T endExclusive, Stream<M> models, int startInclusiveIndex, int endExclusiveIndex);

		default PartConsumer<T, M> toPartConsumer(){
			var cp = new AtomicInteger();
			return (sp, ep, model, min)-> accept(sp, ep, model, cp.get(), cp.incrementAndGet());
		}

		default PartConsumer<T, M> toPartConsumer(int step, ChronoUnit unit){
			return toPartConsumer(step, unit, o-> (Temporal)o); //do not use Temporal.class::cast
		}

		default PartConsumer<T, M> toPartConsumer(int step, ChronoUnit unit, Function<T, ? extends Temporal> fn){
			return (sp, ep, model, min)-> accept(sp, ep, model, 
					(int) nStepBetween(fn.apply(min), fn.apply(sp), step, unit), 
					(int) nStepBetween(fn.apply(min), fn.apply(ep), step, unit));
		}
		
		static <T extends Comparable<? super T>, M extends Interval<T>> IndexPartConsumer<T, M> appender(Collection<MultiModelPart<M>> parts){
			return (sp, ep, model, si, se)-> parts.add(new MultiModelPart<>(model.collect(toList()), si, se, sp, ep));
		}
	}
}
