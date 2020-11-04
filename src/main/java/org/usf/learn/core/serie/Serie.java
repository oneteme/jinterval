package org.usf.learn.core.serie;

import static org.usf.learn.core.util.Asserts.assertEquals;
import static org.usf.learn.core.util.Asserts.requirePositif;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.usf.learn.core.Period;

public interface Serie<T extends Temporal & Comparable<? super T>, P> extends Period<T> {

	int getStep();
	
	List<P> getPoints();
	
	default T getExclusifEnd() {
		return temporalOf(getPoints().size());
	}

	default T inclusifEnd() {
		return getPoints().isEmpty() ? getStart() : temporalOf(getPoints().size()-1);
	}
	
	default P getPoint(int index) {
		return getPoints().get(index);
	}
	
	default int indexOfInstant(T temporal) {
		
		return pointsBetween(temporal);
	}
	
	default SimpleSerie<T, P> subSerie(T fromTemporal, T toTemporal) {
		
		return new SimpleSerie<>(fromTemporal, getStep(), getTemporalUnit(), 
				getPoints().subList(indexOfInstant(fromTemporal), indexOfInstant(toTemporal)));
	}
	
	default <U, R> SimpleSerie<T, R> apply(SimpleSerie<T, U> s, BiFunction<P, U, R> fn) {
		assertEquals(getStep(), s.getStep());
		assertEquals(getTemporalUnit(), s.getTemporalUnit());
		long diff = getStart().until(s.getStart(), getTemporalUnit());
		if(diff % getStep() != 0) {
			throw new IllegalArgumentException("distinct series");
		}
		int index = (int) (Math.abs(diff)/ getStep());
		int lIdx = 0, rIdx = -index, size;
		if(diff == 0) {
			size = Math.min(getPoints().size(), s.getPoints().size());
		}
		else if(diff > 0) {
			if(getPoints().size() <= index) {
				throw new IllegalArgumentException("Unconnected series");
			}
			size = Math.min(getPoints().size(), index + s.getPoints().size());
		}
		else {
			if(s.getPoints().size() <= index) {
				throw new IllegalArgumentException("Unconnected series");
			}
			size = Math.min(index + getPoints().size(), s.getPoints().size());
			lIdx = -index;
			rIdx = 0;
		}
		ArrayList<R> c = new ArrayList<>(size - index);
		for(int i=index; i<size; i++) {
			c.add(fn.apply(getPoints().get(i+lIdx), s.getPoints().get(i+rIdx)));
		}
		return new SimpleSerie<>(diff <= 0 ? getStart() : s.getStart(), getStep(), getTemporalUnit(), c);		
	}

	default <R> SimpleSerie<T, R> join(int step, PointReduce<P, R> reduce) {
		if(requirePositif(step) % this.getStep() > 0) {
			throw new IllegalArgumentException("bad step");
		}
		int loop = step / this.getStep();
		if(getPoints().size() % loop > 0) {
			throw new RuntimeException("bad size");
		}
		ArrayList<R> c = new ArrayList<>(getPoints().size() / loop);
		for(int i=0; i<getPoints().size();) {
			c.add(reduce.reduce(i, i+=loop, getPoints()));
		}
		return new SimpleSerie<>(getStart(), step, getTemporalUnit(), c);	
	}

	default <R> SimpleSerie<T, R> fork(int step, BiConsumer<P, List<R>> consumer) {
		if(this.getStep() % requirePositif(step) > 0) {
			throw new IllegalArgumentException("bad step");
		}
		ArrayList<R> c = new ArrayList<>(getPoints().size() * this.getStep() / step);
		getPoints().forEach(p-> consumer.accept(p, c));
		return new SimpleSerie<>(getStart(), step, getTemporalUnit(), c);	
	}
	
	interface PointReduce<P, R> {
		
		R reduce(int start, int exclusifEnd, List<P> c);
	}
	
	default void forEach(BiConsumer<T, P> consumer) {
		for(int i=0; i<getPoints().size(); i++) {
			consumer.accept(temporalOf(i), getPoints().get(i));
		}
	}
	
	@SuppressWarnings("unchecked")
	private T temporalOf(int index) {

		return (T) getStart().plus(index * getStep(), getTemporalUnit());
	}

	private int pointsBetween(Temporal t) {

		return (int)(getStart().until(t, getTemporalUnit()) / getStep());
	}
}
