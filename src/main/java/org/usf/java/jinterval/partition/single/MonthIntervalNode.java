package org.usf.java.jinterval.partition.single;

import static java.time.LocalTime.MIN;

import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

import org.usf.java.jinterval.core.ImmutableInterval;
import org.usf.java.jinterval.core.Interval;

/**
 * 
 * @author u$f
 *
 */
public final class MonthIntervalNode<M> extends CyclicIntervalNode<M, Month> {
	
	public MonthIntervalNode(M model, Month start, Month exclusifEnd, List<Node<M>> childrens) {
		super(model, start, exclusifEnd, childrens);
	}
	
	@Override
	Interval<ZonedDateTime> adjustInterval(ZonedDateTime zdt) {
		if(containsField(zdt.getMonth())) {
			if(invertedInterval()) {
				return zdt.getMonth().compareTo(startInclusive()) < 0
						? adjust(zdt.minusYears(1), zdt)
						: adjust(zdt, zdt.plusYears(1));
			}
			return adjust(zdt, zdt);
		}
		if(regularInterval()) {
			return zdt.getMonth().compareTo(startInclusive()) < 0
					? adjust(zdt, zdt)
					: adjust(zdt.plusYears(1), zdt.plusYears(1));
		}
		return adjust(zdt, zdt.plusYears(1));
	}
	
	@Override
	ZonedDateTime jump(ZonedDateTime zdt, Month field) {
		return zdt.plusYears(1);
	}
	
	private Interval<ZonedDateTime> adjust(ZonedDateTime start, ZonedDateTime end) {
		return new ImmutableInterval<>(start.with(startInclusive()).with(MIN), end.with(endExclusive()).with(MIN));
	}
	
}
