package org.usf.jinterval.calendar;

import java.time.Month;
import java.util.List;

import org.usf.jinterval.core.CyclicInterval;
import org.usf.jinterval.core.Intervals;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthIntervalSchedule<T> implements CyclicInterval<Month> {
	
	private final Month start;
	private final Month exclusifEnd;
	private final List<DayIntervalSchedule<T>> days;

	@Override
	public String toString() {
		return Intervals.toString(this);
	}
}
