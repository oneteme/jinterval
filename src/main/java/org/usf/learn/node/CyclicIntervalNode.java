package org.usf.learn.node;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class CyclicIntervalNode<M, T> extends Node<M> {
	
	private final T start;
	private final T exclusifEnd;
	private final long duration;

	public CyclicIntervalNode(M model, T start, T exclusifEnd, long duration, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, childrens);
		this.start = start;
		this.exclusifEnd = exclusifEnd;
		this.duration = duration;
	}
	
	@Override
	protected long compareTo(ZonedDateTime zdt) {
		if(duration == 0) {
			return Integer.MAX_VALUE;
		}
		if(duration > 0) {
			ZonedDateTime zEnd = combine(zdt, exclusifEnd);
			long v = zdt.until(zEnd, SECONDS);
			return v > 0 ? v : zEnd.plusDays(1).minusSeconds(duration).until(zdt, SECONDS);
		}
		ZonedDateTime zStart = combine(zdt, start);
		long v = zStart.until(zdt, SECONDS);
		return v < 0 ? v : zStart.plusDays(1).minusSeconds(-duration).until(zdt, SECONDS);
 	}
	
	protected abstract ZonedDateTime combine(ZonedDateTime zdt, T field);

}
