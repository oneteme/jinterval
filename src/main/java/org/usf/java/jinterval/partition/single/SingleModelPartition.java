package org.usf.java.jinterval.partition.single;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
		.map(e-> new SingleModelGroupPart<>(e.getKey(), chainParts(e.getValue())))
		.collect(toList()));
	}
	
	static final <M> int[][] chainParts(List<SingleModelPart<M>> parts){ //same model
		var arr = new LinkedList<int[]>();
		int i = 1;
		while(i<parts.size()) {
			var prv = parts.get(i-1);
			var nxt = parts.get(i);
			if(prv.getEndExclusiveIndex() == nxt.getStartIndex()) {
				arr.add(new int[] {prv.getStartIndex(), nxt.getEndExclusiveIndex()});
				i+=2;
			}
			else {
				arr.add(new int[] {prv.getStartIndex(), prv.getEndExclusiveIndex()});
				i++;
			}
		}
		if(i==parts.size()) {
			arr.add(new int[] {parts.get(i-1).getStartIndex(), parts.get(i-1).getEndExclusiveIndex()});
		}
		return arr.toArray(int[][]::new);
	}
	
	
}
