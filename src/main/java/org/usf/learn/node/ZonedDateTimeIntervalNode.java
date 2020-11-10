package org.usf.learn.node;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public final class ZonedDateTimeIntervalNode<M> extends Node<M>  {

	private final ZonedDateTime exclusifEnd;
	private final long duration;
	
	public ZonedDateTimeIntervalNode(M model, ZonedDateTime start, ZonedDateTime exclusifEnd, List<Node<M>> childrens) {
		super(model, childrens);
		this.exclusifEnd = exclusifEnd;
		this.duration = start.until(exclusifEnd, SECONDS);
	}
	
	@Override
	protected long compareTo(ZonedDateTime date) {
		
		long v = date.until(exclusifEnd, SECONDS);
		if(v <= 0) {
			return 0;
		}
		return v > duration ? duration - v : v;
	}
	
	public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
	
	public static <M> ZonedDateTimeIntervalNode<M> ofInstant(M model, Instant start, Instant exclusifEnd, List<Node<M>> childrens) {
		
		return new ZonedDateTimeIntervalNode<>(model, 
				ZonedDateTime.ofInstant(start, DEFAULT_ZONE_ID), 
				ZonedDateTime.ofInstant(exclusifEnd, DEFAULT_ZONE_ID), 
				childrens);
	}

	public static <M> ZonedDateTimeIntervalNode<M> ofLocal(M model, LocalDateTime start, LocalDateTime exclusifEnd, List<Node<M>> childrens) {
		
		return new ZonedDateTimeIntervalNode<>(model, 
				ZonedDateTime.of(start, DEFAULT_ZONE_ID),
				ZonedDateTime.of(exclusifEnd, DEFAULT_ZONE_ID), 
				childrens);
	}
	
	public static <M> ZonedDateTimeIntervalNode<M> ofLocal(M model, LocalDate start, LocalDate exclusifEnd, List<Node<M>> childrens) {
		
		return new ZonedDateTimeIntervalNode<>(model, 
				start.atStartOfDay(DEFAULT_ZONE_ID), 
				exclusifEnd.atStartOfDay(DEFAULT_ZONE_ID), 
				childrens);
	}

}
