package org.usf.jinterval.partition;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public final class MismatchPartition extends RuntimeException {
	
	private final Object start;
	private final Object exclusifEnd;
	private final int modelCount;
	
	public MismatchPartition(Object start, Object exclusifEnd, int modelCount) {
		super("mismatch partition");
		this.start = start;
		this.exclusifEnd = exclusifEnd;
		this.modelCount = modelCount;
	}
	
}
