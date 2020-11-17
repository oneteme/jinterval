package org.usf.jpartition.calendar;

import java.time.LocalTime;

import org.usf.jpartition.core.CyclicInterval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TimeInterval implements CyclicInterval<LocalTime> {

	private final LocalTime start;
	private final LocalTime exclusifEnd;
}
