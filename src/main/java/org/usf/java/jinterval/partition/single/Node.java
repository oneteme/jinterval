package org.usf.java.jinterval.partition.single;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.usf.java.jinterval.partition.single.SingleModelPart.PARTITON_COMPARATOR;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
 * 
 * @author u$f
 *
 */
@Getter
public class Node<M> {

	private final M model; //nullable
	private final List<Node<M>> childrens;

	public Node(M model, List<Node<M>> childrens) {
		this.model = model;
		this.childrens = ofNullable(childrens).orElseGet(Collections::emptyList);
	}

	public final SingleModelPartition<M> partitions(ZonedDateTime start, ZonedDateTime endExclusive, int step) {
		return partitions(start, endExclusive, step, null);
	}

	public final SingleModelPartition<M> partitions(ZonedDateTime start, ZonedDateTime endExclusive, int step, Collection<Node<M>> primary) {

		var limit = stepDuration(requireNonNull(start), requireNonNull(endExclusive), step);
		var parts = apply(start, 0, limit, step);
		parts.sort(PARTITON_COMPARATOR);
		if(primary != null && !primary.isEmpty()) {
			var over = primary.stream().flatMap(n-> n.apply(start, 0, limit, step).stream())
					.sorted(PARTITON_COMPARATOR)
					.collect(toList());
			parts = SingleModelPart.assign(parts, over);
		}
		return new SingleModelPartition<>(start.toInstant(), endExclusive.toInstant(), step, parts);
	}

	List<SingleModelPart<M>> apply(ZonedDateTime zdt, int from, int to, int step){
		return getModel() == null && getChildrens().isEmpty() 
				? emptyList() 
				: deepApply(zdt, from, to, step);
	}
	
	final List<SingleModelPart<M>> deepApply(ZonedDateTime zdt, int from, int to, int step) {
		
		if(getChildrens().isEmpty()) {
			return singletonList(new SingleModelPart<>(from, to, getModel()));
		}
		var parts = getChildrens().stream().flatMap(n-> n.apply(zdt, from, to, step).stream()).collect(toList());
		var tmp = new ArrayList<SingleModelPart<M>>();
		if(getModel() != null && !parts.isEmpty()) {
			parts.sort(PARTITON_COMPARATOR);
			if(parts.get(0).getStartIndex() > from) {
				tmp.add(new SingleModelPart<>(from, parts.get(0).getStartIndex(), getModel()));
			}
			parts.stream().reduce((prv, nxt)-> {
				if(nxt.getStartIndex() > prv.getEndExclusiveIndex()) {
					tmp.add(new SingleModelPart<>(prv.getEndExclusiveIndex(), nxt.getStartIndex(), getModel()));
				}
				return nxt;
			});
			if(parts.get(parts.size()-1).getEndExclusiveIndex() < to) {
				tmp.add(new SingleModelPart<>(parts.get(parts.size()-1).getEndExclusiveIndex(), to, getModel()));
			}
			parts.addAll(tmp); //optim 
		}
		return parts;
	}
	
	static int stepDuration(ZonedDateTime start, ZonedDateTime exclusifEnd, int step){
		
		return (int) (start.until(exclusifEnd, SECONDS) / step);
	}
}
