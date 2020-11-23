package org.usf.jinterval.calendar;

import java.time.DayOfWeek;
import java.util.List;

import org.usf.jinterval.core.CyclicInterval;
import org.usf.jinterval.core.Intervals;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DayIntervalSchedule<T> implements CyclicInterval<DayOfWeek> {

	private final DayOfWeek start;
	private final DayOfWeek exclusifEnd;
	private final List<TimeIntervalSchedule<T>> timeParts;
	
	@Override
	public String toString() {
		return Intervals.toString(this);
	}
}
