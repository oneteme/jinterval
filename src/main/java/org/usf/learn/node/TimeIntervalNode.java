package org.usf.learn.node;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.usf.learn.node.RegularIntervalNode.DEFAULT_ZONE_ID;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

public final class TimeIntervalNode<M> extends CyclicIntervalNode<M, LocalTime> {
	
	public TimeIntervalNode(M model, LocalTime start, LocalTime exclusifEnd, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, start, exclusifEnd, (s, e)-> s.until(e, SECONDS), childrens);
	}
	
	@Override
	protected ZonedDateTime combine(ZonedDateTime zdt, LocalTime field) {

		return zdt.toLocalDate()
				.atTime(field)
				.atZone(DEFAULT_ZONE_ID);
	}
}
