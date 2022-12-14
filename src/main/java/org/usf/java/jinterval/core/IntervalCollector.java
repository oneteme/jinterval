package org.usf.java.jinterval.core;

import static java.util.Collections.emptySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntervalCollector<T extends Comparable<? super T>> implements Collector<Interval<T>, List<T>, Optional<Interval<T>>> {
	
	private final BinaryOperator<T> startOp;
	private final BinaryOperator<T> exclusifEndOp;
	
	@Override
	public Supplier<List<T>> supplier() {
		return ()-> new ArrayList<>(2);
	}

	@Override
	public BiConsumer<List<T>, Interval<T>> accumulator() {
		return (acc,o)-> {
			if(o.invertedInterval()) {
				throw new IllegalArgumentException("inverted interval");
			}
			if(acc.isEmpty()) {
				acc.add(o.startInclusive());
				acc.add(o.endExclusive());
			}
			else {
				acc.set(0, startOp.apply(acc.get(0), o.startInclusive()));
				acc.set(1, exclusifEndOp.apply(acc.get(1), o.endExclusive()));
			}
		};
	}

	@Override
	public BinaryOperator<List<T>> combiner() {
		return (a,b)-> {
			a.set(0, startOp.apply(a.get(0), b.get(0)));
			a.set(1, exclusifEndOp.apply(a.get(1), b.get(1)));
			return a;
		};
	}

	@Override
	public Function<List<T>, Optional<Interval<T>>> finisher() {

		return a-> a.isEmpty() || a.get(0).compareTo(a.get(1))>=0
				? Optional.empty() 
				: Optional.of(new ImmutableInterval<>(a.get(0), a.get(1)));
	}

	@Override
	public Set<Characteristics> characteristics() {

		return emptySet();
	}
	
	public static final <T extends Comparable<? super T>> IntervalCollector<T> largestInterval() {

		return new IntervalCollector<>(IntervalUtils::min, IntervalUtils::max);
	}
	
	public static final <T extends Comparable<? super T>> IntervalCollector<T> smallestInterval() {

		return new IntervalCollector<>(IntervalUtils::max, IntervalUtils::min);
	}
	
}