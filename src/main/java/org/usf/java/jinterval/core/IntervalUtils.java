package org.usf.java.jinterval.core;

import static java.time.DayOfWeek.MONDAY;

import java.time.DayOfWeek;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntervalUtils {
	
	public static <T extends Comparable<? super T>> int direction(T start, T exclusifEnd){
		return exclusifEnd.compareTo(start);
	}
	
	public static <T extends Comparable<? super T>> int requiredPositifDirection(T start, T exclusifEnd){
		int d = direction(start, exclusifEnd);
		if(d <= 0) {
			throw new IllegalArgumentException("start >= exclusifEnd");
		}
		return d;
	}
	
	public static <T extends Comparable<? super T>> void requiredPositifDirection(Collection<? extends Interval<T>> intervals){
		
		intervals.forEach(i-> requiredPositifDirection(i.startInclusive(), i.endExclusive()));
	}
	
	public static String toString(int start, int exclusifEnd){
		return "[" + start + ", " + exclusifEnd  + "[";
	}
	
	public static String toString(Interval<?> i){
		return toString(i.startInclusive(), i.endExclusive());
	}
	
	public static String toString(Object start, Object exclusifEnd){
		return "[" + start + ", " + exclusifEnd  + "[";
	}

	public static <T extends Comparable<? super T>> T min(T a, T b){
		return a.compareTo(b) <= 0 ? a : b;
	}

	public static <T extends Comparable<? super T>> T max(T a, T b){
		return a.compareTo(b) >= 0 ? a : b;
	}
	
	public static void main(String[] args) {
		
		System.out.println(direction(DayOfWeek.FRIDAY, DayOfWeek.MONDAY));
		System.out.println(direction(DayOfWeek.FRIDAY, DayOfWeek.SUNDAY));
		
	}

}