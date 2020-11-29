package org.usf.jinterval.core;

import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntervalFilters {

	public static <T extends Comparable<? super T>> Predicate<T> in(Interval<T> interval){

		return interval::containsField;
	}
	
	public static <T extends Comparable<? super T>> Predicate<T> in(T start, T exclusifEnd){

		Interval<T> interval = ImmutableInterval.of(start, exclusifEnd);
		return interval::containsField;
	}
	
	public static <T extends Comparable<? super T>> Predicate<T> out(Interval<T> interval){

		return in(interval).negate();
	}
	
	public static <T extends Comparable<? super T>> Predicate<T> out(T start, T exclusifEnd){

		return in(start, exclusifEnd).negate();
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> inside(Interval<T> interval){

		return interval::containsInterval;
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> inside(T start, T exclusifEnd){

		Interval<T> interval = ImmutableInterval.of(start, exclusifEnd);
		return interval::containsInterval;
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> contains(Interval<T> interval){
		
		return i-> i.containsInterval(interval);
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> contains(T start, T exclusifEnd){
		
		return i-> i.containsInterval(start, exclusifEnd);
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> intersect(Interval<T> interval){
		
		return i-> i.intersectInterval(interval);
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> intersect(T start, T exclusifEnd){
		
		return i-> i.intersectInterval(start, exclusifEnd);
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> diverge(Interval<T> interval){
		
		Predicate<I> pr = intersect(interval);
		return pr.negate();
	}
	
	public static <T extends Comparable<? super T>, I extends Interval<T>> Predicate<I> diverge(T start, T exclusifEnd){

		Predicate<I> pr = intersect(start, exclusifEnd);
		return pr.negate();
	}
	
}
