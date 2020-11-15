package org.usf.learn.node;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class PeriodPartitionMap<M> {
	
	private final Instant start;
	private final Instant exclusifEnd;
	private final List<PeriodPartition<M>> partitions;

}
