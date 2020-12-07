package org.usf.jinterval.core;

import static java.time.DayOfWeek.*;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.usf.jinterval.core.ImmutableInterval.of;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lombok.RequiredArgsConstructor;

interface IntervalCollectionFactory {

	static Stream<Arguments> dayOfWeek() {

	    return Stream.of(
	
			new IntervalCollectionTestCase<DayOfWeek>(true,
					Arrays.asList(of(MONDAY, MONDAY)), 
					Collections.emptyList(), 
					Collections.emptyList()),
			
			new IntervalCollectionTestCase<DayOfWeek>(true,
					Arrays.asList(of(TUESDAY, SATURDAY), of(SATURDAY, TUESDAY)), 
					Collections.emptyList(), 
					Collections.emptyList()),
		
			new IntervalCollectionTestCase<DayOfWeek>(true,
					Arrays.asList(of(WEDNESDAY, MONDAY)), 
					Arrays.asList(of(MONDAY, WEDNESDAY)),
					Collections.emptyList()),
		
			new IntervalCollectionTestCase<DayOfWeek>(true,
					Arrays.asList(of(MONDAY, TUESDAY), of(WEDNESDAY, THURSDAY), of(SATURDAY, SUNDAY)), 
					Arrays.asList(of(TUESDAY, WEDNESDAY), of(THURSDAY, SATURDAY), of(SUNDAY, MONDAY)), 
					Collections.emptyList()),
		
			new IntervalCollectionTestCase<DayOfWeek>(true,
					Arrays.asList(of(MONDAY, MONDAY), of(MONDAY, MONDAY)), 
					Collections.emptyList(), 
					Arrays.asList(of(MONDAY, MONDAY))),

			new IntervalCollectionTestCase<DayOfWeek>(true,
					Arrays.asList(of(TUESDAY, SATURDAY), of(FRIDAY, MONDAY)), 
					Arrays.asList(of(MONDAY, TUESDAY)), 
					Arrays.asList(of(FRIDAY, SATURDAY))),

			new IntervalCollectionTestCase<DayOfWeek>(true,
					Arrays.asList(of(MONDAY, WEDNESDAY), of(TUESDAY, FRIDAY), of(SATURDAY, TUESDAY)), 
					Arrays.asList(of(FRIDAY, SATURDAY)), 
					Arrays.asList(of(MONDAY, TUESDAY), of(TUESDAY, WEDNESDAY)))
	    	
	    	).map( Arguments::arguments);
	}
	


	static Stream<Arguments> localDate() {
		
		LocalDate min = LocalDate.of(2020, 1, 1);
		LocalDate max = LocalDate.of(2020, 1, 31);

	    return Stream.of(
	
			new IntervalCollectionTestCase<LocalDate>(false,
					Arrays.asList(of(min, max)), 
					Collections.emptyList(), 
					Collections.emptyList()),
		
			new IntervalCollectionTestCase<LocalDate>(false,
					Arrays.asList(of(min, LocalDate.of(2020, 01, 15)), of(LocalDate.of(2020, 01, 20), max)), 
					Arrays.asList(of(LocalDate.of(2020, 01, 15), LocalDate.of(2020, 01, 20))),
					Collections.emptyList()),

			new IntervalCollectionTestCase<LocalDate>(false,
					Arrays.asList(of(min, LocalDate.of(2020, 01, 15)), of(LocalDate.of(2020, 01, 10), max)), 
					Collections.emptyList(),
			Arrays.asList(of(LocalDate.of(2020, 01, 10), LocalDate.of(2020, 01, 15)))),
			
			new IntervalCollectionTestCase<LocalDate>(false,
					Arrays.asList(of(min, LocalDate.of(2020, 01, 15)), of(LocalDate.of(2020, 01, 20), max), of(LocalDate.of(2020, 01, 17), LocalDate.of(2020, 02, 05))), 
					Arrays.asList(of(LocalDate.of(2020, 01, 15), LocalDate.of(2020, 01, 17))),
					Arrays.asList(of(LocalDate.of(2020, 1, 20), LocalDate.of(2020, 01, 31))))
	    	
	    	).map( Arguments::arguments);
	}
	

	@RequiredArgsConstructor
	static class IntervalCollectionTestCase<T extends Comparable<? super T>> {

		final boolean cyclic;
		final List<Interval<T>> intervals;
		final List<Interval<T>> missing;
		final List<Interval<T>> overlap;
		
		boolean missingFirst() {
			return overlap.isEmpty() 
					|| !missing.isEmpty() 
					&& missing.get(0).getStart().compareTo(overlap.get(0).getStart()) < 0;
		}
		@Override
		public String toString() {
			return intervals.toString();
		}
	}
}
