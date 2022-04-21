package org.usf.java.jinterval.partition.single;

import static java.lang.Math.min;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.usf.java.jinterval.core.Interval;

/**
 * 
 * @author u$f
 *
 */
public final class RegularIntervalNode<M> extends Node<M> implements Interval<ZonedDateTime>  {

	private final ZonedDateTime startInclusive;
	private final ZonedDateTime endExclusive;
	
	RegularIntervalNode(M model, ZonedDateTime start, ZonedDateTime endExclusive, List<Node<M>> childrens) {
		super(model, childrens);
		this.startInclusive = start;
		this.endExclusive = endExclusive;
		if(start.compareTo(endExclusive) >= 0) {
			throw new IllegalArgumentException("start >= exclusifEnd");
		}
	}
	
	@Override
	public ZonedDateTime startInclusive() {
		return startInclusive;
	}

	@Override
	public ZonedDateTime endExclusive() {
		return endExclusive;
	}
	
	@Override
	List<SingleModelPart<M>> apply(ZonedDateTime zdt, int from, int to, int step) {
		var r = stepDuration(zdt, endExclusive, step);
		if(r <= 0) {
			return emptyList();
		}
		to = min(to, from + r);
		var l = stepDuration(startInclusive, zdt, step);
		if(l < 0) {
			from += -l;
			if(from >= to) {
				return emptyList();
			}
		}
		return deepApply(zdt, from, to, step);
	}
	
	public static <M> RegularIntervalNode<M> ofInstant(M model, Instant start, Instant exclusifEnd, List<Node<M>> childrens) {
		
		var zoneId = systemDefault();
		return new RegularIntervalNode<>(model, 
				ZonedDateTime.ofInstant(requireNonNull(start), zoneId), 
				ZonedDateTime.ofInstant(requireNonNull(exclusifEnd), zoneId), 
				childrens);
	}

	public static <M> RegularIntervalNode<M> ofLocal(M model, LocalDateTime start, LocalDateTime exclusifEnd, List<Node<M>> childrens) {

		var zoneId = systemDefault();
		return new RegularIntervalNode<>(model, 
				ZonedDateTime.of(requireNonNull(start), zoneId),
				ZonedDateTime.of(requireNonNull(exclusifEnd), zoneId), 
				childrens);
	}
	
	public static <M> RegularIntervalNode<M> ofLocal(M model, LocalDate start, LocalDate exclusifEnd, List<Node<M>> childrens) {

		var zoneId = systemDefault();
		return new RegularIntervalNode<>(model, 
				requireNonNull(start).atStartOfDay(zoneId), 
				requireNonNull(exclusifEnd).atStartOfDay(zoneId), 
				childrens);
	}
	public static <M> RegularIntervalNode<M> ofYear(M model, int start, int exclusifEnd, List<Node<M>> childrens) {

		var zoneId = systemDefault();
		return new RegularIntervalNode<>(model, 
				LocalDate.of(start, 1, 1).atStartOfDay(zoneId), 
				LocalDate.of(exclusifEnd, 1, 1).atStartOfDay(zoneId), 
				childrens);
	}
	
}
