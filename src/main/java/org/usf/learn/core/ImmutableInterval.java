package org.usf.learn.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImmutableInterval<T extends Comparable<? super T>> implements Interval<T> {
	
	private final T start;
	private final T exclusifEnd;

}
