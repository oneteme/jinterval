package org.usf.jinterval.core;

import java.util.function.BiFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
final class IntervalShiftingProxy<T extends Comparable<? super T>> implements Interval<T> {
	
	private final Interval<T> interval;
	private final BiFunction<T, T, Interval<T>> factory;
	private final BiFunction<T, Integer, T> fn;

	T shiftStart(int v){
		return fn.apply(getStart(), v);
	}
	T shiftExclusifEnd(int v){
		return fn.apply(getExclusifEnd(), v);
	}

	IntervalShiftingProxy<T> shift(int v1, int v2){
		return copy(factory.apply(shiftStart(v1), shiftExclusifEnd(v2)));
	}
	IntervalShiftingProxy<T> shiftStart(int v1, int v2){
		return copy(factory.apply(shiftStart(v1), shiftStart(v2)));
	}
	IntervalShiftingProxy<T> shiftExclusifEnd(int v1, int v2){
		return copy(factory.apply(shiftExclusifEnd(v1), shiftExclusifEnd(v2)));
	}

	IntervalShiftingProxy<T> copy(T start, T exclusifEnd){
		return copy(factory.apply(start, exclusifEnd));
	}
	IntervalShiftingProxy<T> copy(Interval<T> inverval){
		return new IntervalShiftingProxy<>(inverval, factory, fn);
	}
	IntervalShiftingProxy<T> reverseInterval() {
		return this.reverseInterval(this::copy);
	}
	
	/** Delegate public methods **/
	
	public T getStart() {
		return interval.getStart();
	}
	public T getExclusifEnd() {
		return interval.getExclusifEnd();
	}
	public boolean containsField(T temporal) {
		return interval.containsField(temporal);
	}
	public boolean containsInterval(Interval<T> p) {
		return interval.containsInterval(p);
	}
	public boolean containsInterval(T start, T exclusifEnd) {
		return interval.containsInterval(start, exclusifEnd);
	}
	public boolean intersectInterval(Interval<T> p) {
		return interval.intersectInterval(p);
	}
	public boolean intersectInterval(T start, T exclusifEnd) {
		return interval.intersectInterval(start, exclusifEnd);
	}
	public int direction() {
		return interval.direction();
	}
	public boolean isRegular() {
		return interval.isRegular();
	}
	public boolean isInverted() {
		return interval.isInverted();
	}
	public <I extends Interval<T>> I reverseInterval(BiFunction<? super T, ? super T, I> fn) {
		return interval.reverseInterval(fn);
	}
	public boolean symmetrical(Interval<T> val) {
		return interval.symmetrical(val);
	}
	public boolean equals(Object obj) {
		return interval.equals(obj);
	}
	public int hashCode() {
		return interval.hashCode();
	}
	public String toString() {
		return interval.toString();
	}
}
