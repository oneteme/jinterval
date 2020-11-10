package org.usf.learn.calendar;

import java.time.LocalTime;

import org.usf.learn.core.CyclicInterval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TimeInterval implements CyclicInterval<LocalTime> {

	private final LocalTime start;
	private final LocalTime exclusifEnd;
}
