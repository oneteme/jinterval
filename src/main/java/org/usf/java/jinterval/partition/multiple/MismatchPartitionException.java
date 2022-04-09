package org.usf.java.jinterval.partition.multiple;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
final class MismatchPartitionException extends RuntimeException {
	
	private final Object start;
	private final Object exclusifEnd;
	private final int modelCount;
	
	public MismatchPartitionException(Object start, Object exclusifEnd, int modelCount) {
		super("mismatch partition");
		this.start = start;
		this.exclusifEnd = exclusifEnd;
		this.modelCount = modelCount;
	}
	
}
