package org.usf.jinterval.partition.single;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.usf.jinterval.partition.single.SingleModelPart.PARTITON_COMPARATOR;
import static org.usf.jinterval.partition.single.SingleModelPart.assign;
import static org.usf.jinterval.util.CollectionUtils.requireNonNullElseEmptyList;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class Node<M> {

	private final M model; //nullable
	private final List<? extends Node<M>> childrens;

	public Node(M model, List<? extends Node<M>> childrens) {
		this.model = model;
		this.childrens = requireNonNullElseEmptyList(childrens);
	}

	public final SingleModelPartition<M> partitions(ZonedDateTime start, ZonedDateTime exclusifEnd, int step) {

		return partitions(start, exclusifEnd, step, emptyList());
	}

	public final SingleModelPartition<M> partitions(ZonedDateTime start, ZonedDateTime exclusifEnd, int step, Collection<? extends RegularIntervalNode<M>> primaryNodes) {
		int limit = (int)(requireNonNull(start).until(requireNonNull(exclusifEnd), SECONDS)/step);
		if(limit <= 0) {
			throw new IllegalArgumentException("start >= end");
		}
		List<SingleModelPart<M>> primaryPartitions = requireNonNull(primaryNodes).stream()
				.flatMap(n-> n.until(start, 0, limit, step).stream()) //fix limit
				.collect(Collectors.toList());
		List<SingleModelPart<M>> partitions = until(start, 0, limit, step); //parallel
		return new SingleModelPartition<>(start.toInstant(), exclusifEnd.toInstant(), step, 
				primaryPartitions.isEmpty() ? partitions : assign(partitions, primaryPartitions));
	}

	protected List<SingleModelPart<M>> until(ZonedDateTime date, int index, int max, int step) {

		List<SingleModelPart<M>> list = new LinkedList<>();
		long diff = compareTo(date);
		while(index < max && diff != 0) {
			int ends = (int) (diff / step);
			if(diff > 0) {

				ends = Math.min(index+ends, max);
				SingleModelPart<ZonedDateTime> context = new SingleModelPart<>(index, ends, date); //final reference
				List<SingleModelPart<M>> childList = childrens.parallelStream() //parallel ?
						.flatMap(n-> n.until(context.getModel(), context.getStartIndex(), context.getExclusifEndIndex(), step).stream())
						.sequential()
						.sorted(PARTITON_COMPARATOR)
						.collect(Collectors.toList());
				if(model != null) {
					if(childList.isEmpty()) {
						list.add(new SingleModelPart<>(context.getStartIndex(), context.getExclusifEndIndex(), model));
					}
					else {
						if(context.getStartIndex() < childList.get(0).getStartIndex()) {
							list.add(new SingleModelPart<>(context.getStartIndex(), childList.get(0).getStartIndex(), model));
						}
						list.add(childList.get(0));
						for(int i=1; i<childList.size(); i++) {
							if(childList.get(i-1).getExclusifEndIndex() < childList.get(i).getStartIndex()) {
								list.add(new SingleModelPart<>(childList.get(i-1).getExclusifEndIndex(), childList.get(i).getStartIndex(), model));
							}
							list.add(childList.get(i));
						}
						if(childList.get(childList.size()-1).getExclusifEndIndex() < context.getExclusifEndIndex()) {
							list.add(new SingleModelPart<>(childList.get(childList.size()-1).getExclusifEndIndex(), context.getExclusifEndIndex(), model));
						}
					}
				}
				else {
					list.addAll(childList);
				}
			}
			else {
				if(index-ends > max) {
					break; //optimization
				}
				diff = -diff;
				ends = index-ends;
			}
			date = date.plus(diff, SECONDS); //jump to next
			index = ends;
			diff = compareTo(date);
		}
		return list;
	}

	protected long compareTo(ZonedDateTime date) {

		return Integer.MAX_VALUE;
	}
}
