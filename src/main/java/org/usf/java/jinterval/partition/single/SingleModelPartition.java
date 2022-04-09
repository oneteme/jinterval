package org.usf.java.jinterval.partition.single;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 *
 */
@Getter
@RequiredArgsConstructor
public final class SingleModelPartition<M> {
	
	private final Instant start;
	private final Instant exclusifEnd;
	private final int step;
	private final Collection<SingleModelPart<M>> partitions;
	
	public SingleModelGroupPartition<M> groupByModel(){
		
		return new SingleModelGroupPartition<>(start, exclusifEnd, step, partitions.stream()
		.collect(groupingBy(SingleModelPart::getModel)).entrySet().stream()
		.map(e-> new SingleModelGroupPart<>(e.getKey(), e.getValue().stream()
				.map(p-> new int[] {p.startIndex, p.endExclusiveIndex})
				.toArray(int[][]::new)))
		.collect(toList()));
	}
	
	
}
