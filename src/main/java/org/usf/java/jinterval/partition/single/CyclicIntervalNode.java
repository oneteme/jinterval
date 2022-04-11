package org.usf.java.jinterval.partition.single;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import org.usf.java.jinterval.core.Interval;
import org.usf.java.jinterval.core.IntervalUtils;

/**
 * 
 * @author u$f
 *
 */
public abstract class CyclicIntervalNode<M, T extends Comparable<? super T>> extends Node<M> implements Interval<T> {
	
	private final T startInclusive;
	private final T endExclusive;
	private final int direction;

	CyclicIntervalNode(M model, T startInclusive, T endExclusive, List<Node<M>> childrens) {//ZoneOffset ?
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
	
	@Override
	public int intervalDirection() {
		return direction;
	}

	abstract Interval<ZonedDateTime> adjustInterval(ZonedDateTime zdt);
	
	abstract ZonedDateTime jump(ZonedDateTime zdt, T field);
	
	@Override
	public List<SingleModelPart<M>> apply(ZonedDateTime zdt, int from, int to, int step) {
		if(getModel() == null && getChildrens().isEmpty()) {
			return emptyList();
		}
		if(fullInterval()) {
			return deepApply(zdt, from, to, step);
		}
		var in = adjustInterval(zdt);
		var prv = in.startInclusive();
		var sft = stepDuration(zdt, prv, step); //+/-
		int idx1 = from + Math.max(0, sft);
		if(idx1 < to) {
			var list = new LinkedList<SingleModelPart<M>>();
			var nxt = in.endExclusive();
			int idx2 = idx1 + stepDuration(sft > 0 ? prv : zdt, nxt, step);
			list.addAll(deepApply(sft > 0 ? prv : zdt, idx1, Math.min(to, idx2), step));
			prv = jump(prv, startInclusive());
			idx1 = idx2 + stepDuration(nxt, prv, step);
			nxt = jump(nxt, endExclusive());
			idx2 = idx1 + stepDuration(prv, nxt, step);
			while(idx1 < to) {
				list.addAll(deepApply(prv, idx1, Math.min(to, idx2), step));
				prv = jump(prv, startInclusive());
				idx1 = idx2 + stepDuration(nxt, prv, step);
				nxt = jump(nxt, endExclusive());
				idx2 = idx1 + stepDuration(prv, nxt, step);
			}
			return list;
		}
		return emptyList();
	}
	
	@Override
	public String toString() {
		return getModel() + " : " + IntervalUtils.toString(this);
	}


}
