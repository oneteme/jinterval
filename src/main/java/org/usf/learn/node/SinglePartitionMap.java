package org.usf.learn.node;

import static java.util.Collections.emptySet;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
final class SinglePartitionMap<M> {
	
	private final Instant start;
	private final Instant exclusifEnd;
	private final Collection<SinglePartition<M>> partitions;
	
	public PeriodPartitionMap<M> groupByModel(){
		
		return new PeriodPartitionMap<>(start, exclusifEnd, partitions.stream()
				.collect(Collectors.groupingBy(SinglePartition::getModel, new Collector<SinglePartition<M>, List<int[]>, int[][]>(){

			@Override
			public Supplier<List<int[]>> supplier() {
				return LinkedList::new;
			}

			@Override
			public BiConsumer<List<int[]>, SinglePartition<M>> accumulator() {
				return (a,p)-> a.add(new int[]{p.start, p.exclusifEnd});
			}

			@Override
			public BinaryOperator<List<int[]>> combiner() {
				return (a,b)-> {a.addAll(b); return a;};
			}

			@Override
			public Function<List<int[]>, int[][]> finisher() {
				return a-> a.toArray(int[][]::new);
			}

			@Override
			public Set<Characteristics> characteristics() {
				return emptySet();
			}
			
		})).entrySet().stream()
				.map(e-> new PeriodPartition<M>(e.getKey(), e.getValue()))
				.collect(Collectors.toList()));
	}

}
