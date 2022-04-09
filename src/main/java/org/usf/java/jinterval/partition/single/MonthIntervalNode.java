package org.usf.java.jinterval.partition.single;

import java.time.LocalDate;
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
	
	public MonthIntervalNode(M model, Month start, Month exclusifEnd) {
		super(model, start, exclusifEnd, null);
	}
	
	public MonthIntervalNode(M model, Month start, Month exclusifEnd, List<Node<M>> childrens) {
		super(model, start, exclusifEnd, childrens);
	}
	
	@Override
	Interval<ZonedDateTime> adjustInterval(ZonedDateTime zdt) {
		if(containsField(zdt.getMonth())) {
			if(invertedInterval()) {
				return zdt.getMonth().compareTo(startInclusive()) < 0
						? adjust(zdt, zdt.getYear()-1, zdt.getYear())
						: adjust(zdt, zdt.getYear(), zdt.getYear()+1);
			}
			return adjust(zdt, zdt.getYear(), zdt.getYear());
		}
		if(regularInterval()) {
			return zdt.getMonth().compareTo(startInclusive()) < 0
					? adjust(zdt, zdt.getYear(), zdt.getYear())
					: adjust(zdt, zdt.getYear()+1, zdt.getYear()+1);
		}
		return adjust(zdt, zdt.getYear(), zdt.getYear()+1);
	}
	
	@Override
	ZonedDateTime jump(ZonedDateTime zdt, Month field) {
		return zdt.plusYears(1);
	}
	
	private Interval<ZonedDateTime> adjust(ZonedDateTime zdt, int y1, int y2) {
		return new ImmutableInterval<>(
				LocalDate.of(y1, startInclusive(), 1).atStartOfDay(zdt.getZone()), 
				LocalDate.of(y2, endExclusive(), 1).atStartOfDay(zdt.getZone()));
	}
	
}
