package org.usf.java.jinterval.partition.single;

import static java.lang.Math.min;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 
 * @author u$f
 *
 */
public final class RegularIntervalNode<M> extends Node<M>  {
	
	public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

	private final ZonedDateTime startInclusive;
	private final ZonedDateTime endExclusive;
	private final long duration;
	
	RegularIntervalNode(M model, ZonedDateTime start, ZonedDateTime endExclusive, List<Node<M>> childrens) {
		super(model, childrens);
		this.startInclusive = start;
		this.endExclusive = endExclusive;
		this.duration = start.until(endExclusive, SECONDS);
		if(duration <= 0) {
			throw new IllegalArgumentException("start >= exclusifEnd");
		}
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
		
		return new RegularIntervalNode<>(model, 
				ZonedDateTime.ofInstant(requireNonNull(start), DEFAULT_ZONE_ID), 
				ZonedDateTime.ofInstant(requireNonNull(exclusifEnd), DEFAULT_ZONE_ID), 
				childrens);
	}

	public static <M> RegularIntervalNode<M> ofLocal(M model, LocalDateTime start, LocalDateTime exclusifEnd, List<Node<M>> childrens) {
		
		return new RegularIntervalNode<>(model, 
				ZonedDateTime.of(requireNonNull(start), DEFAULT_ZONE_ID),
				ZonedDateTime.of(requireNonNull(exclusifEnd), DEFAULT_ZONE_ID), 
				childrens);
	}
	
	public static <M> RegularIntervalNode<M> ofLocal(M model, LocalDate start, LocalDate exclusifEnd, List<Node<M>> childrens) {
		
		return new RegularIntervalNode<>(model, 
				requireNonNull(start).atStartOfDay(DEFAULT_ZONE_ID), 
				requireNonNull(exclusifEnd).atStartOfDay(DEFAULT_ZONE_ID), 
				childrens);
	}
	public static <M> RegularIntervalNode<M> ofYear(M model, int start, int exclusifEnd, List<Node<M>> childrens) {
		
		return new RegularIntervalNode<>(model, 
				LocalDate.of(start, 1, 1).atStartOfDay(DEFAULT_ZONE_ID), 
				LocalDate.of(exclusifEnd, 1, 1).atStartOfDay(DEFAULT_ZONE_ID), 
				childrens);
	}
	
}
