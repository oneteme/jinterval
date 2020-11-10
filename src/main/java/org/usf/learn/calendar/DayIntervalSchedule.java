package org.usf.learn.calendar;

import java.time.DayOfWeek;
import java.util.List;

import org.usf.learn.core.CyclicInterval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DayIntervalSchedule<T> implements CyclicInterval<DayOfWeek> {

	private final DayOfWeek start;
	private final DayOfWeek exclusifEnd;
	private final List<TimeIntervalSchedule<T>> timeParts;
	
}
