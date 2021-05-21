package org.usf.jinterval.partition.single;

import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.List;

public final class DayOfWeekIntervalNode<M> extends CyclicIntervalNode<M, DayOfWeek>  {

	public DayOfWeekIntervalNode(M model, DayOfWeek start, DayOfWeek exclusifEnd, List<Node<M>> childrens) {
		super(model, start, exclusifEnd, childrens);
	}

	@Override
	protected ZonedDateTime adjustStart(ZonedDateTime zdt) {
		return adjust(zdt, containsField(zdt.toLocalDate().getDayOfWeek())
				? previousOrSame(startInclusive()) 
				: next(startInclusive()));
	}

	@Override
	protected ZonedDateTime adjustExlusifEnd(ZonedDateTime zdt) {

		return adjust(zdt, next(this.endExclusive()));
	}
	
	private ZonedDateTime adjust(ZonedDateTime zdt, TemporalAdjuster adj) {
		
		return zdt.with(adj).with(LocalTime.MIN);
	}
	
}
