package org.usf.jinterval.core;

import static java.util.Collections.emptySet;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.usf.jinterval.core.IntervalCollector.SimpleInterval;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntervalCollector<T extends Comparable<? super T>> implements Collector<Interval<T>, SimpleInterval<T>, Optional<Interval<T>>>{
	
	private final BinaryOperator<T> startOp;
	private final BinaryOperator<T> exclusifEndOp;
	
	@Override
	public Supplier<SimpleInterval<T>> supplier() {
		return SimpleInterval::new;
	}

	@Override
	public BiConsumer<SimpleInterval<T>, Interval<T>> accumulator() {
		return (a,i)-> {
			if(a.start == null) {
				a.set(i.getStart(), i.getExclusifEnd());
			}
			else {
				a.set(startOp.apply(a.getStart(), i.getStart()), 
					exclusifEndOp.apply(a.getExclusifEnd(), i.getExclusifEnd()));
			}
		};
	}

	@Override
	public BinaryOperator<SimpleInterval<T>> combiner() {
		return (a,i)-> a.set(
				startOp.apply(a.getStart(), i.getStart()), 
				exclusifEndOp.apply(a.getExclusifEnd(), i.getExclusifEnd()));
	}

	@Override
	public Function<SimpleInterval<T>, Optional<Interval<T>>> finisher() {

		return a-> a.start == null || a.start.compareTo(a.exclusifEnd) >= 0 
				? Optional.empty() 
				: Optional.of(a);
	}

	@Override
	public Set<Characteristics> characteristics() {

		return emptySet();
	}
	
	public static final <T extends Comparable<? super T>> IntervalCollector<T> maxInterval() {

		return new IntervalCollector<>(Intervals::min, Intervals::max);
	}
	
	public static final <T extends Comparable<? super T>> IntervalCollector<T> minInterval() {

		return new IntervalCollector<>(Intervals::max, Intervals::min);
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	static final class SimpleInterval<T extends Comparable<? super T>> implements RegularInterval<T> {
	
		private T start;
		private T exclusifEnd;

		SimpleInterval<T> set(T start, T exclusifEnd){
			this.start = start;
			this.exclusifEnd = exclusifEnd;
			return this;
		}
	}
}