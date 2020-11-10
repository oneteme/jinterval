package org.usf.learn.partition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PartitionMap<P, T> {
	
	private final List<Partition<P, ?, T>> partitions;
	
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

	public PartitionMap<P,T> filterEmptyModelPartitions() {

		return filterPartitions(s-> s == 0);
	}
	
	public PartitionMap<P,T> filterSingleModelPartitions() {

		return filterPartitions(s-> s == 1);
	}
	
	public PartitionMap<P,T> filterMultiModelPartitions() {

		return filterPartitions(s-> s > 1);
	}
	
	public PartitionMap<P,T> filterPartitions(IntPredicate modelSizePredicate) {

		return new PartitionMap<>(filterPartitionList(modelSizePredicate)
				.collect(Collectors.toList()));
	}
	
	public PartitionMap<P,T> requiredNotEmptyModelPartitions(){
		return requiredEmpty(s-> s == 0);
	}
	
	public PartitionMap<P,T> requiredSingleModelPartitions(){
		return requiredEmpty(s-> s != 1);
	}

	private PartitionMap<P,T> requiredEmpty(IntPredicate sizePredicate){
		if(filterPartitionList(sizePredicate).count() > 0) {
			throw new IllegalArgumentException("");
		}
		return this;
	}
	
	private Stream<? extends Partition<P,?,T>> filterPartitionList(IntPredicate modelSizePredicate) {
		return partitions.stream().filter(p-> modelSizePredicate.test(p.modelCount()));
	}
}
