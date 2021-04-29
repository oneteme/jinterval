package org.usf.jinterval.partition.single;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.List;

import org.usf.jinterval.core.Interval;
import org.usf.jinterval.core.IntervalUtils;

import lombok.Getter;

@Getter
public abstract class CyclicIntervalNode<M, T extends Comparable<? super T>> extends Node<M> implements Interval<T> {
	
	private final T start;
	private final T exclusifEnd;
	private final int direction;

	public CyclicIntervalNode(M model, T start, T exclusifEnd, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, childrens);
		this.start = requireNonNull(start);
		this.exclusifEnd = requireNonNull(exclusifEnd);
		this.direction = IntervalUtils.direction(start, exclusifEnd);
	}

	protected abstract ZonedDateTime adjustStart(ZonedDateTime zdt);
	
	protected abstract ZonedDateTime adjustExlusifEnd(ZonedDateTime zdt);
	
	@Override
	protected long compareTo(ZonedDateTime zdt) {
		if(direction != 0) {
			long v = zdt.until(adjustStart(zdt), SECONDS);
			if(v > 0) {
				return -v;
			}
			v = zdt.until(adjustExlusifEnd(zdt), SECONDS);
			if(v > 0) {
				return v;
			}
			throw new RuntimeException("unreachable code");
		}
		return super.compareTo(zdt);//MAX
 	}
	
	@Override
	public String toString() {
		return getModel() + " : " + IntervalUtils.toString(start, exclusifEnd);
	}

}
