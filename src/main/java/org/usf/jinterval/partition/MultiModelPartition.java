package org.usf.jinterval.partition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.usf.jinterval.core.exception.MissingIntervalException;
import org.usf.jinterval.core.exception.OverlapIntervalException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MultiModelPartition<P, T> {
	
	private static final IntPredicate EMPTY_PARTTTION_FILTER = i-> i == 0;
	private static final IntPredicate SINGLE_MODEL_PARTITION_FILTER = i-> i == 1;
	private static final IntPredicate MULTI_MODEL_PARTITION_FILTER   = i-> i > 1;
	
	private final List<MultiModelPart<P, ?, T>> partitions;
	
	public List<T> collect(BinaryOperator<T> op){
		return collect(op, ArrayList::new);
	}
	
	public List<T> collect(BinaryOperator<T> op, Supplier<List<T>> supplier){
		List<T> list = supplier.get();
		accept(op, list::add);
		return list;
	}
	
	public void accept(BinaryOperator<T> op, Consumer<T> consumer){
		partitions.forEach(p-> p.accept(op, consumer));
	}
	
	public <R> List<R> collect(R identity, BiFunction<R, T, R> fn){
		return collect(identity, fn, ArrayList::new);
	}
	
	public <R> List<R> collect(R identity, BiFunction<R, T, R> fn, Supplier<List<R>> supplier){
		List<R> list = supplier.get();
		accept(identity, fn, list::add);
		return list;
	}
	
	public <R> void accept(R identity, BiFunction<R, T, R> fn, Consumer<R> consumer){
		partitions.forEach(p-> p.accept(identity, fn, consumer));
	}

	
	public MultiModelPartition<P,T> emptyPartitions() {

		return filterPartitions(EMPTY_PARTTTION_FILTER);
	}
	
	public MultiModelPartition<P,T> singleModelPartitions() {

		return filterPartitions(SINGLE_MODEL_PARTITION_FILTER);
	}
	
	public MultiModelPartition<P,T> multiModelPartitions() {

		return filterPartitions(MULTI_MODEL_PARTITION_FILTER);
	}
	
	public MultiModelPartition<P,T> filterPartitions(IntPredicate modelSizePredicate) {

		return new MultiModelPartition<>(filterPartitionList(modelSizePredicate)
				.collect(Collectors.toList()));
	}
	
	public MultiModelPartition<P,T> requiredNotEmptyPartitions(){
		return requiredEmpty(EMPTY_PARTTTION_FILTER);
	}
	
	public MultiModelPartition<P,T> requiredSingleModelPartitions(){
		return requiredEmpty(SINGLE_MODEL_PARTITION_FILTER.negate());
	}

//	public MultiModelPartition<P,T> requiredMultiModelPartitions(){
//		return requiredEmpty(MULTI_MODEL_PARTITION_FILTER.negate());
//	}

	private MultiModelPartition<P,T> requiredEmpty(IntPredicate sizePredicate){
		var c = filterPartitionList(sizePredicate).findAny();
		if(c.isPresent()) {
			throw c.get().modelCount() == 0 
					? new MissingIntervalException(c.get().getStart(), c.get().getExclusifEnd()) 
					: new OverlapIntervalException(c.get().getStart(), c.get().getExclusifEnd());
		}
		return this;
	}
	
	private Stream<? extends MultiModelPart<P,?,T>> filterPartitionList(IntPredicate modelSizePredicate) {
		return partitions.stream().filter(p-> modelSizePredicate.test(p.modelCount()));
	}
}
