package org.usf.jinterval.partition.single;

import static java.time.LocalTime.MIN;
import static org.usf.jinterval.partition.single.RegularIntervalNode.DEFAULT_ZONE_ID;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

public final class MonthIntervalNode<M> extends CyclicIntervalNode<M, Integer> {
	
	public MonthIntervalNode(M model, Month start, Month exclusifEnd, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, start.getValue(), exclusifEnd.getValue(), (s, e)-> e-s, childrens);
	}
	
	@Override
	protected ZonedDateTime combine(ZonedDateTime zdt, Integer field) {

		return ZonedDateTime.of(LocalDate.of(zdt.getYear(), field, 1), MIN, DEFAULT_ZONE_ID);
	}
}
