package org.usf.jinterval.partition.single;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.ToLongBiFunction;

public abstract class CyclicIntervalNode<M, T> extends Node<M> {
	
	private final T start;
	private final T exclusifEnd;
	private final long duration;

	public CyclicIntervalNode(M model, T start, T exclusifEnd, ToLongBiFunction<T, T> durationFn, List<Node<M>> childrens) {//ZoneOffset ?
		super(model, childrens);
		this.start = requireNonNull(start);
		this.exclusifEnd = requireNonNull(exclusifEnd);
		this.duration = requireNonNull(durationFn).applyAsLong(start, exclusifEnd);
	}
	
	@Override
	protected long compareTo(ZonedDateTime zdt) {
		if(duration > 0) {
			ZonedDateTime zEnd = combine(zdt, exclusifEnd);
			long v = zdt.until(zEnd, SECONDS);
			return v > 0 ? v : zEnd.plusDays(1).minusSeconds(duration).until(zdt, SECONDS);
		}
		else if(duration < 0)  {
			ZonedDateTime zStart = combine(zdt, start);
			long v = zStart.until(zdt, SECONDS);
			return v < 0 ? v : zStart.plusDays(1).plusSeconds(-duration).until(zdt, SECONDS);
		}
		return super.compareTo(zdt);//MAX
 	}
	
	protected abstract ZonedDateTime combine(ZonedDateTime zdt, T field);

}
