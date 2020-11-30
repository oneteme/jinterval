package org.usf.jinterval.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class IntervalFiltersTest implements CommonTestInterval  {
	
	@Override //test in / out
	public <T extends Comparable<? super T>> void testContainsField(boolean expected, IntervalShiftingProxy<T> ip, T field) {

		assertEquals(expected, IntervalFilters.in(ip).test(field));
		assertEquals(expected, IntervalFilters.in(ip.getStart(), ip.getExclusifEnd()).test(field));

		assertEquals(!expected, IntervalFilters.out(ip).test(field));
		assertEquals(!expected, IntervalFilters.out(ip.getStart(), ip.getExclusifEnd()).test(field));
	}

	@Override //test contains / inside
	public <T extends Comparable<? super T>> void testContainsInterval(boolean expected, IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval) {
		
		assertEquals(expected, IntervalFilters.contains(interval).test(ip));
		assertEquals(expected, IntervalFilters.contains(interval.getStart(), interval.getExclusifEnd()).test(ip));
		
		assertEquals(expected, IntervalFilters.inside(ip).test(interval));
		assertEquals(expected, IntervalFilters.inside(ip.getStart(), ip.getExclusifEnd()).test(interval));
	}

	@Override //test intersect / diverge
	public <T extends Comparable<? super T>> void testIntersectInterval(boolean expected, IntervalShiftingProxy<T> ip, IntervalShiftingProxy<T> interval) {

		assertEquals(expected, IntervalFilters.intersect(interval).test(ip));
		assertEquals(expected, IntervalFilters.intersect(interval.getStart(), interval.getExclusifEnd()).test(ip));
		
		assertEquals(!expected, IntervalFilters.diverge(ip).test(interval));
		assertEquals(!expected, IntervalFilters.diverge(ip.getStart(), ip.getExclusifEnd()).test(interval));
	}
	
	@Override
	public <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd) {
		return ImmutableInterval.of(start, exclusifEnd);
	}
}
