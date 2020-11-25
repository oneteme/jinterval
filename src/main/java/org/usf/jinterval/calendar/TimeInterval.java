package org.usf.jinterval.calendar;

import java.time.LocalTime;

import org.usf.jinterval.core.Interval;
import org.usf.jinterval.core.Intervals;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TimeInterval implements Interval<LocalTime> {

	private final LocalTime start;
	private final LocalTime exclusifEnd;

	@Override
	public String toString() {
		return Intervals.toString(this);
	}
}
