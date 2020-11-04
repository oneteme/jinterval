package org.usf.learn.exception;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class OverlapIntervalException extends RuntimeException {

	private final Object start;
	private final Object exclusifEnd;
	
	public OverlapIntervalException(Object start, Object exclusifEnd) {
		super("overlap interval");
		this.start = start;
		this.exclusifEnd = exclusifEnd;
	}

}
