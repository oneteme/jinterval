package org.usf.jinterval.node;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public final class RegularIntervalNode<M> extends Node<M>  {

	private final ZonedDateTime exclusifEnd;
	private final long duration;
	
	public RegularIntervalNode(M model, ZonedDateTime start, ZonedDateTime exclusifEnd, List<Node<M>> childrens) {
		super(model, childrens);
		this.exclusifEnd = requireNonNull(exclusifEnd);
		this.duration = requireNonNull(start).until(exclusifEnd, SECONDS);
		if(duration <= 0) {
			throw new IllegalArgumentException("start > exclusifEnd");
		}
	}
	
	@Override
	protected long compareTo(ZonedDateTime date) {
		
		long v = date.until(exclusifEnd, SECONDS);
		if(v <= 0) {
			return 0;
		}
		return v > duration ? duration-v : v;
	}
	
	public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
	
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
