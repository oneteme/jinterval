package org.usf.jinterval.partition.single;

import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

public final class MonthIntervalNode<M> extends CyclicIntervalNode<M, Integer> {
	
	public MonthIntervalNode(M model, Month start, Month exclusifEnd, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, start.getValue(), exclusifEnd.getValue(), childrens);
	}

	@Override
	protected ZonedDateTime adjustStart(ZonedDateTime zdt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ZonedDateTime adjustExlusifEnd(ZonedDateTime zdt) {
		// TODO Auto-generated method stub
		return null;
	}
}
