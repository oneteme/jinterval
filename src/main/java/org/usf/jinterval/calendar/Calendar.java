package org.usf.jinterval.calendar;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Calendar<T> {

	private final String id;
	private final List<MonthIntervalSchedule<T>> seasons;
	private final List<DaySchedule<T>> specialDays;
	
}
