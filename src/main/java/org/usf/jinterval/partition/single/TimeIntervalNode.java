package org.usf.jinterval.partition.single;

import static org.usf.jinterval.partition.single.RegularIntervalNode.DEFAULT_ZONE_ID;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

public final class TimeIntervalNode<M> extends CyclicIntervalNode<M, LocalTime> {
	
	public TimeIntervalNode(M model, LocalTime start, LocalTime exclusifEnd, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, start, exclusifEnd, childrens);
	}
	
	@Override
	protected ZonedDateTime adjustStart(ZonedDateTime zdt) {
		
		int shift = getDirection() > 0 && zdt.toLocalTime().compareTo(getExclusifEnd()) >=0 ? 1 : 0;
		return zdt.toLocalDate().plusDays(shift)
				.atTime(getStart())
				.atZone(DEFAULT_ZONE_ID);
	}
	
	@Override
	protected ZonedDateTime adjustExlusifEnd(ZonedDateTime zdt) {

		return zdt.toLocalDate().plusDays(getDirection() > 0 ? 0 : 1)
				.atTime(getExclusifEnd())
				.atZone(DEFAULT_ZONE_ID);
	}
	
}
