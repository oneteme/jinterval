package org.usf.learn.core;

import static java.util.Comparator.naturalOrder;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.function.BinaryOperator.minBy;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.usf.learn.core.IntervalCollector.SimpleInterval;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

		return a-> a.start == null ? Optional.empty() : Optional.of(a);
	}

	@Override
	public Set<Characteristics> characteristics() {

		return Set.of(Characteristics.IDENTITY_FINISH);
	}
	
	public static final <T extends Comparable<? super T>> IntervalCollector<T> maxInterval() {
		Comparator<T> comparator = naturalOrder();
		return new IntervalCollector<>(minBy(comparator), maxBy(comparator));
	}
	
	public static final <T extends Comparable<? super T>> IntervalCollector<T> minInterval() {
		Comparator<T> comparator = naturalOrder();
		return new IntervalCollector<>(maxBy(comparator), minBy(comparator));
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter(value = AccessLevel.PACKAGE)
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