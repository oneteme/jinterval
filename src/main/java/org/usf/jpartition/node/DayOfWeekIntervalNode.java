package org.usf.jpartition.node;

import static org.usf.jpartition.node.RegularIntervalNode.DEFAULT_ZONE_ID;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;

public final class DayOfWeekIntervalNode<M> extends CyclicIntervalNode<M, Integer>  {

	public DayOfWeekIntervalNode(M model, DayOfWeek start, DayOfWeek exclusifEnd, List<Node<M>> childrens) {
		super(model, start.getValue(), exclusifEnd.getValue(), (s, e)-> e-s, childrens);
	}
	
	@Override
	protected ZonedDateTime combine(ZonedDateTime zdt, Integer field) {
		return zdt.toLocalDate()
				.plusDays(zdt.toLocalDate().getDayOfWeek().getValue() - field)
				.atStartOfDay(DEFAULT_ZONE_ID);
	}

}
