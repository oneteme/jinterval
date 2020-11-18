package org.usf.jinterval.core.exception;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public final class OverlapIntervalException extends RuntimeException {

	private final Object start;
	private final Object exclusifEnd;
	
	public OverlapIntervalException(Object start, Object exclusifEnd) {
		super("overlap interval");
		this.start = start;
		this.exclusifEnd = exclusifEnd;
	}

}
