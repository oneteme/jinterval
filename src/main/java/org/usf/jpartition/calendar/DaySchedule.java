package org.usf.jpartition.calendar;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DaySchedule<T> {
	
	private final LocalDate date;
	private final List<TimeIntervalSchedule<T>> timeParts;

}
