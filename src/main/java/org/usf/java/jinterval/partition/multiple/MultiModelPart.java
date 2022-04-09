package org.usf.java.jinterval.partition.multiple;

import static lombok.AccessLevel.PACKAGE;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

import org.usf.java.jinterval.core.IntervalUtils;
import org.usf.java.jinterval.partition.Part;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
final class MultiModelPart<T> implements Part<List<T>> {

	private final List<T> model;
	private final int startIndex;
	private final int endExclusiveIndex;
	private final Object start;
	private final Object exclusifEnd;

	public Optional<T> reduce(BinaryOperator<T> op) {
		
		return model.stream().reduce(op);
	}

	public <R> R reduce(R identity, BiFunction<R, T, R> op) {

		R acc = identity;
		for(var col=0; col<model.size(); col++) {
			acc = op.apply(acc, model.get(col));
		}
		return acc;
	}

	public <R> void deepReduce(Function<T, List<R>> fn, BinaryOperator<R> op, Consumer<R> consumer) {
		if(!model.isEmpty()) {
			for(int row=startIndex; row<endExclusiveIndex; row++) {
				R acc = fn.apply(model.get(0)).get(row);
				for(var col=1; col<model.size(); col++) {
					acc = op.apply(acc, fn.apply(model.get(col)).get(row));
				}
				consumer.accept(acc);
			}
		}
	}
	
	public <U, R> void deepReduce(Function<T, List<U>> fn, R identity, BiFunction<R, U, R> op, Consumer<R> consumer) {
		if(model.isEmpty()) {
			for(int row=startIndex; row<endExclusiveIndex; row++) {
				consumer.accept(identity);
			}
		}
		else {
			for(int row=startIndex; row<endExclusiveIndex; row++) {
				R acc = identity;
				for(var col=0; col<model.size(); col++) {
					acc = op.apply(acc, fn.apply(model.get(col)).get(row));
				}
				consumer.accept(acc);
			}
		}
	}
	
	@Override
	public String toString() {
		return IntervalUtils.toString(start, exclusifEnd) 
				+ " => index  : " + IntervalUtils.toString(startIndex, endExclusiveIndex)
				+ " => models : " + model;
	}
}
