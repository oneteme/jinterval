package org.usf.jinterval.core;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Intervals {
	
	public static <T extends Comparable<? super T>> T min(T a, T b){
		return a.compareTo(b) <= 0 ? a : b;
	}

	public static <T extends Comparable<? super T>> T max(T a, T b){
		return a.compareTo(b) >= 0 ? a : b;
	}
	
	public static boolean equals(Interval<?> a, Interval<?> b) {
		return Objects.equals(a.getStart(), b.getStart()) 
				&& Objects.equals(a.getExclusifEnd(), b.getExclusifEnd());
	}
	
	public static String toString(Interval<?> i){
		return toString(i.getStart(), i.getExclusifEnd());
	}
	
	public static String toString(Object start, Object exclusifEnd){
		return "[" + start + ", " + exclusifEnd  + "[";
	}

}
