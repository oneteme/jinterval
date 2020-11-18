package org.usf.jinterval.core.exception;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public final class MissingIntervalException extends RuntimeException {

	private final Object start;
	private final Object exclusifEnd;
	
	public MissingIntervalException(Object start, Object exclusifEnd) {
		super("missing interval");
		this.start = start;
		this.exclusifEnd = exclusifEnd;
	}
	
}
