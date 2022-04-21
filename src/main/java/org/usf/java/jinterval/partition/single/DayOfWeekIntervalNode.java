package org.usf.java.jinterval.partition.single;

import static java.time.LocalTime.MIN;
import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;

import org.usf.java.jinterval.core.ImmutableInterval;
import org.usf.java.jinterval.core.Interval;

/**
 * 
 * @author u$f
 *
 */
public final class DayOfWeekIntervalNode<M> extends CyclicIntervalNode<M, DayOfWeek>  {

	public DayOfWeekIntervalNode(M model, DayOfWeek start, DayOfWeek exclusifEnd) {
		super(model, start, exclusifEnd, null);
	}
	
	public DayOfWeekIntervalNode(M model, DayOfWeek start, DayOfWeek exclusifEnd, List<Node<M>> childrens) {
		super(model, start, exclusifEnd, childrens);
	}
	
	@Override
	Interval<ZonedDateTime> adjustInterval(ZonedDateTime zdt) {
		var adj = containsField(zdt.getDayOfWeek()) 
				? previousOrSame(startInclusive()) 
				: next(startInclusive());
		var start = zdt.with(adj).with(MIN);
		return new ImmutableInterval<>(start, start.with(next(endExclusive())).with(MIN));
	}
	
	@Override
	ZonedDateTime jump(ZonedDateTime zdt, DayOfWeek filed) {
		return zdt.plusWeeks(1);
	}

}
