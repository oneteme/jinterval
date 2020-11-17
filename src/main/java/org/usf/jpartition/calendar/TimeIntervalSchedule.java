package org.usf.jpartition.calendar;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TimeIntervalSchedule<T> {

	private final T model;
	private final List<TimeInterval> times;
}
