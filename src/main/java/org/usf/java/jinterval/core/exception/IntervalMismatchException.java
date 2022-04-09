package org.usf.java.jinterval.core.exception;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public final class IntervalMismatchException extends RuntimeException {
	
	public IntervalMismatchException(String message) {
		super(message);
	}
	
}
