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
	
	private final T startInclusive;
	private final T endExclusive;
	private final int direction;

	public CyclicIntervalNode(M model, T startInclusive, T endExclusive, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, childrens);
		this.startInclusive = requireNonNull(startInclusive);
		this.endExclusive = requireNonNull(endExclusive);
		this.direction = IntervalUtils.direction(startInclusive, endExclusive);
	}
	
	@Override
	public T startInclusive() {
		return startInclusive;
	}
	
	@Override
	public T endExclusive() {
		return endExclusive;
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
		return getModel() + " : " + IntervalUtils.toString(startInclusive, endExclusive);
	}

}
