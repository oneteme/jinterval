package org.usf.learn.core;

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

}
