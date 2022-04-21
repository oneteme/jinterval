package org.usf.java.jinterval.partition.multiple;

import static lombok.AccessLevel.PACKAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
public final class MultiModelPartition<T> {
	
	private static final IntPredicate EMPTY_MODEL_PART_FILTER  = i-> i == 0;
	private static final IntPredicate SINGLE_MODEL_PART_FILTER = i-> i == 1;
	private static final IntPredicate MULTI_MODEL_PART_FILTER  = i-> i > 1;
	
	private final List<MultiModelPart<T>> partitions;
	
	public List<T> collect(BinaryOperator<T> op){
		return partitions.stream()
				.map(p-> p.reduce(op))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	public <R> List<R> collect(R identity, BiFunction<R, T, R> op){
		return partitions.stream()
				.map(p-> p.reduce(identity, op))
				.collect(Collectors.toList());
	}
	
	public <R> List<R> deepReduce(Function<T, List<R>> fn, BinaryOperator<R> op) {
		List<R> arr = new ArrayList<>(deepSize());
		partitions.forEach(p-> p.deepReduce(fn, op, arr::add));
		return arr;
	}
	
	public <U, R> List<R> deepReduce(Function<T, List<U>> fn, R identity, BiFunction<R, U, R> op) {
		List<R> arr = new ArrayList<>(deepSize());
		partitions.forEach(p-> p.deepReduce(fn, identity, op, arr::add));
		return arr;
	}
	
	private int deepSize() {
		return partitions.stream()
				.mapToInt(p-> p.getEndExclusiveIndex() - p.getStartIndex())
				.sum();
	}
	
	public MultiModelPartition<T> emptyPartitions() {

		return filterPartitions(EMPTY_MODEL_PART_FILTER);
	}
	
	public MultiModelPartition<T> singleModelPartitions() {

		return filterPartitions(SINGLE_MODEL_PART_FILTER);
	}
	
	public MultiModelPartition<T> multiModelPartitions() {

		return filterPartitions(MULTI_MODEL_PART_FILTER);
	}
	
	public MultiModelPartition<T> filterPartitions(IntPredicate modelSizePredicate) {

		return new MultiModelPartition<>(filterPartitionList(modelSizePredicate)
				.collect(Collectors.toList()));
	}
	
	public MultiModelPartition<T> requiredNotEmptyPartitions(){
		return requiredEmpty(EMPTY_MODEL_PART_FILTER);
	}
	
	public MultiModelPartition<T> requiredSingleModelPartitions(){
		return requiredEmpty(SINGLE_MODEL_PART_FILTER.negate());
	}

	public MultiModelPartition<T> requiredEmpty(IntPredicate nPartitionModel){
		var p = filterPartitionList(nPartitionModel).findAny();
		if(p.isPresent()) {
			MultiModelPart<T> part = p.get();
			throw new MismatchPartitionException(part.getStart(), part.getExclusifEnd(), part.getModel().size()) ;
		}
		return this;
	}
	
	private Stream<MultiModelPart<T>> filterPartitionList(IntPredicate modelSizePredicate) {
		return partitions.stream().filter(p-> modelSizePredicate.test(p.getModel().size()));
	}
}
