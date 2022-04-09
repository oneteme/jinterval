package org.usf.java.jinterval.partition.single;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.usf.java.jinterval.core.IntervalUtils.requiredPositifDirection;

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
	private final long duration;
	
	public RegularIntervalNode(M model, ZonedDateTime start, ZonedDateTime exclusifEnd, List<Node<M>> childrens) {
		super(model, childrens);
		this.startInclusive = start;
		this.duration = requiredPositifDirection(requireNonNull(start), requireNonNull(exclusifEnd));
	}

	@Override
	List<SingleModelPart<M>> apply(ZonedDateTime zdt, int from, int to, int step) {
		var diff = startInclusive.until(zdt, SECONDS);
		if(diff >= duration) {		
			return emptyList();
		}
		var idx1 = from + diff / step;
		if(idx1 > to) {	
			return emptyList();
		}
		var idx2 = Math.min(to, idx1 + duration/step);
		return deepApply(zdt, (int)idx1, (int)idx2, step);
	}
	
	public static <M> RegularIntervalNode<M> ofInstant(M model, Instant start, Instant exclusifEnd, List<Node<M>> childrens) {
		
		return new RegularIntervalNode<>(model, 
				ZonedDateTime.ofInstant(start, DEFAULT_ZONE_ID), 
				ZonedDateTime.ofInstant(exclusifEnd, DEFAULT_ZONE_ID), 
				childrens);
	}

	public static <M> RegularIntervalNode<M> ofLocal(M model, LocalDateTime start, LocalDateTime exclusifEnd, List<Node<M>> childrens) {
		
		return new RegularIntervalNode<>(model, 
				ZonedDateTime.of(start, DEFAULT_ZONE_ID),
				ZonedDateTime.of(exclusifEnd, DEFAULT_ZONE_ID), 
				childrens);
	}
	
	public static <M> RegularIntervalNode<M> ofLocal(M model, LocalDate start, LocalDate exclusifEnd, List<Node<M>> childrens) {
		
		return new RegularIntervalNode<>(model, 
				start.atStartOfDay(DEFAULT_ZONE_ID), 
				exclusifEnd.atStartOfDay(DEFAULT_ZONE_ID), 
				childrens);
	}
	public static <M> RegularIntervalNode<M> ofYear(M model, int start, int exclusifEnd, List<Node<M>> childrens) {
		
		return new RegularIntervalNode<>(model, 
				LocalDate.of(start, 1, 1).atStartOfDay(DEFAULT_ZONE_ID), 
				LocalDate.of(exclusifEnd, 1, 1).atStartOfDay(DEFAULT_ZONE_ID), 
				childrens);
	}
	
}
