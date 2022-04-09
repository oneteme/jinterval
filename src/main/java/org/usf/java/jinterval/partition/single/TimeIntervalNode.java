package org.usf.java.jinterval.partition.single;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.usf.java.jinterval.core.ImmutableInterval;
import org.usf.java.jinterval.core.Interval;

/**
 * 
 * @author u$f
 *
 */
public final class TimeIntervalNode<M> extends CyclicIntervalNode<M, LocalTime> {
	
	public TimeIntervalNode(M model, LocalTime start, LocalTime exclusifEnd) {//ZoneOffset ?
		super(model, start, exclusifEnd, null);
	}
	
	public TimeIntervalNode(M model, LocalTime start, LocalTime exclusifEnd, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, start, exclusifEnd, childrens);
	}
	
	@Override
	Interval<ZonedDateTime> adjustInterval(ZonedDateTime zdt) {
		if(containsField(zdt.toLocalTime())) {
			if(invertedInterval()) {
				return zdt.toLocalTime().isBefore(startInclusive())
						? adjust(zdt.minusDays(1), zdt)
						: adjust(zdt, zdt.plusDays(1));
			}
			return adjust(zdt, zdt);
		}
		if(regularInterval()) {
			return zdt.toLocalTime().isBefore(startInclusive()) 
					? adjust(zdt, zdt)
					: adjust(zdt.plusDays(1), zdt.plusDays(1));
		}
		return adjust(zdt, zdt.plusDays(1));
	}
	
	@Override
	ZonedDateTime jump(ZonedDateTime zdt, LocalTime field) {
		return zdt.plusDays(1);
	}
	
	private Interval<ZonedDateTime> adjust(ZonedDateTime start, ZonedDateTime end) {
		return new ImmutableInterval<>(start.with(startInclusive()), end.with(endExclusive()));
	}
}
