package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntervalsTest {

	<T extends Comparable<? super T>> void testToString(IntervalShiftingProxy<T> interval) {//increase test cov
		
		assertEquals("[" + interval.getStart() + ", " + interval.getExclusifEnd() + "[", interval.toString());
	}
}
