package org.usf.java.jinterval.core;

import static java.lang.Math.max;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.usf.java.jinterval.core.TemporalUtils.nStepBetween;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.function.Predicate;


public interface Curve<T> extends TemporalInterval<Instant> {
	
	short secondStep(); 
	
	List<T> points(); 
	
	default T pointAt(int index){
		return points().get(index);
	}
	
	default List<T> subPoints(Instant start, Instant endExclusive){
		return subPeriod(start, endExclusive, (d, t)-> {
			if(d >= 0) {
				return d;
			}
			throw new IllegalArgumentException("out of period " + t);
		});
	}
	
	default List<T> intersectPoints(Instant start, Instant endExclusive){
		return subPeriod(start, endExclusive, (d, t)-> max(0, d));
	}
	
	private List<T> subPeriod(Instant start, Instant endExclusive, BiFunction<Long, Instant, Long> fn) { 
		var d1 = SECONDS.between(startInclusive(), start);
		var d2 = SECONDS.between(endExclusive, endExclusive());
		if(d1 % secondStep() == 0 && d2 % secondStep() == 0) {
			var fromIndex = fn.apply(d1, start) / secondStep();
			var toIndex = points().size() - fn.apply(d2, endExclusive) / secondStep();
			if(fromIndex <= toIndex) { // fromIndex == toIndex => empty ?
				return subPoints((int) fromIndex, (int) toIndex);
			}
		}
		throw new IllegalArgumentException("mismatch period " + IntervalUtils.toString(start, endExclusive));
	}
	
	default List<T> subPoints(int fromIndex, int toIndex){
		return points().subList(fromIndex, toIndex);
	}
	
	default void updateIf(Predicate<T> pr, IntFunction<T> fn) {//TODO BiPredicate, BiFunction
		var points = points();
		for(var i=0; i<points.size(); i++) {
			if(pr.test(points.get(i))) {				
				points.set(i, fn.apply(i));
			}
		}
	}
	
	default List<T> combine(BinaryOperator<T> op, Curve<T> curve){
		if(secondStep() == curve.secondStep() && points().size() == curve.points().size() && equalInterval(curve)) {
			var points = new ArrayList<T>(points().size());
			for(int i=0; i<points().size(); i++) {
				points.add(op.apply(pointAt(i), curve.pointAt(i)));
			}
			return points;
		}
		throw new IllegalArgumentException("mismatch curve");
	}

	default boolean isCompleteCurve() {
		return unitCount() == points().size();
	}
	
	default boolean isMissingPoints() {
		return unitCount() > points().size();
	}
	
	default boolean isOverTakingPoints() {
		return unitCount() < points().size();
	}
	
	private long unitCount() {
		return unitCount(SECONDS, secondStep());
	}
	
	public static <T> List<T> create(Instant start, Instant endExclusive, int step, IntFunction<T> factory){
		var n = (int) nStepBetween(start, endExclusive, step, SECONDS);
		return range(0, n).mapToObj(factory).collect(toList());
	}
	
}
