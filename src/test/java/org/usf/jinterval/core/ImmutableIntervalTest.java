package org.usf.jinterval.core;

public class ImmutableIntervalTest extends IntervalTest {
	
	@Override
	<T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd){
		
		return ImmutableInterval.of(start, exclusifEnd);
	}	

}
