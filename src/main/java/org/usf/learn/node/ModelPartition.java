package org.usf.learn.node;

import static java.util.Collections.emptySet;
import static java.util.Comparator.comparingInt;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor 
class ModelPartition<M> {
	
	static final Comparator<ModelPartition<?>> PARTITON_COMPARATOR = comparingInt(ModelPartition::getStart);
	
	int start;
	int exclusifEnd;
	final M model;
	
	static final <M> List<ModelPartition<M>> assign(List<ModelPartition<M>> l1, List<ModelPartition<M>> l2) {
		
		int ldx = 0;
		int exclusifEnd;
		List<ModelPartition<M>> res = new LinkedList<>();
		for(int i=0; i<l2.size(); i++) {
			
			exclusifEnd = l2.get(i).start;
			while(ldx < l1.size() && l1.get(ldx).exclusifEnd <= exclusifEnd) {
				res.add(l1.get(ldx++));
			}
			if(ldx < l1.size() && l1.get(ldx).start < exclusifEnd) {
				res.add(new ModelPartition<>(l1.get(ldx).start, exclusifEnd, l1.get(ldx).model));
			}
			res.add(l2.get(i));

			exclusifEnd = l2.get(i).exclusifEnd;
			while(ldx < l1.size() && l1.get(ldx).exclusifEnd <= exclusifEnd) {
				ldx++;
			}
			if(ldx < l1.size() && l1.get(ldx).start <= exclusifEnd) {
				l1.get(ldx).setStart(exclusifEnd);
			}
		}
		for(int i=ldx; i<l1.size(); i++) {
			res.add(l1.get(i));
		}
		return res;
	}
	
	static <M> List<ModelFullPartition<M>> of(List<ModelPartition<M>> list){
		
		return list.stream()
				.collect(Collectors.groupingBy(ModelPartition::getModel, new Collector<ModelPartition<M>, List<int[]>, int[][]>(){

			@Override
			public Supplier<List<int[]>> supplier() {
				return LinkedList::new;
			}

			@Override
			public BiConsumer<List<int[]>, ModelPartition<M>> accumulator() {
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
				.map(e-> new ModelFullPartition<M>(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}
	
}