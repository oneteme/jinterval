package org.usf.learn.node;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.usf.learn.node.SinglePartition.PARTITON_COMPARATOR;
import static org.usf.learn.node.SinglePartition.assign;
import static org.usf.learn.util.CollectionUtils.notNullOrEmpty;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class Node<M> {
	
	private final M model;
	private final List<? extends Node<M>> childrens;
	
	public Node(M model, List<? extends Node<M>> childrens) {
		this.model = model; //nullable
		this.childrens = notNullOrEmpty(childrens);
	}
	
	public final SinglePartitionMap<M> partitions(ZonedDateTime start, ZonedDateTime exclusifEnd, int step) {

		return partitions(start, exclusifEnd, step, emptyList());
	}
	
	public final SinglePartitionMap<M> partitions(ZonedDateTime start, ZonedDateTime exclusifEnd, int step, Collection<? extends RegularIntervalNode<M>> primaryNodes) {
		int limit = (int)(requireNonNull(start).until(requireNonNull(exclusifEnd), SECONDS)/step);
		if(limit <= 0) {
			throw new IllegalArgumentException("start >= end");
		}
		List<SinglePartition<M>> primaryPartitions = requireNonNull(primaryNodes).stream()
				.flatMap(n-> n.until(start, 0, limit, step).stream()) //fix limit
				.collect(Collectors.toList());
		List<SinglePartition<M>> partitions = until(start, 0, limit, step); //parallel
		return new SinglePartitionMap<>(start.toInstant(), exclusifEnd.toInstant(), 
				primaryPartitions.isEmpty() ? partitions : assign(partitions, primaryPartitions));
	}
	
	protected List<SinglePartition<M>> until(ZonedDateTime date, int index, int max, int step) {
		
		List<SinglePartition<M>> list = new LinkedList<>();
		long diff = compareTo(date);
		while(diff != 0) {
			int loops = (int) (diff / step);
			if(diff > 0) {
				loops = Math.min(loops, max);
				SinglePartition<ZonedDateTime> context = new SinglePartition<>(index, loops, date); //final reference
				List<SinglePartition<M>> childList = childrens.parallelStream() //parallel ?
						.flatMap(n-> n.until(context.getModel(), context.getStart(), context.getExclusifEnd(), step).stream())
						.sequential()
						.sorted(PARTITON_COMPARATOR)
						.collect(Collectors.toList());
				if(model != null) {
					if(childList.isEmpty()) {
						list.add(new SinglePartition<>(context.getStart(), context.getExclusifEnd(), model));
					}
					else {
						if(context.getStart() < childList.get(0).getStart()) {
							list.add(new SinglePartition<>(context.getStart(), childList.get(0).getStart(), model));
						}
						list.add(childList.get(0));
						for(int i=1; i<childList.size(); i++) {
							if(childList.get(i-1).getExclusifEnd() < childList.get(i).getStart()) {
								list.add(new SinglePartition<>(childList.get(i-1).getExclusifEnd(), childList.get(i).getStart(), model));
							}
							list.add(childList.get(i));
						}
						if(childList.get(childList.size()-1).getExclusifEnd() < context.getExclusifEnd()) {
							list.add(new SinglePartition<>(childList.get(childList.size()-1).getExclusifEnd(), context.getExclusifEnd(), model));
						}
					}
				}
				else {
					list.addAll(childList);
				}
			}
			else {
				if(-loops > max) {
					break;
				}
				diff = -diff;
				loops = -loops;
			}
			date = date.plus(diff, SECONDS); //jump to next
			index += loops;
			diff = compareTo(date);
		}
		return list;
	}
		
	protected long compareTo(ZonedDateTime date) {
		
		return Integer.MAX_VALUE;
	}
}
