package org.usf.learn.node;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class Node<M> {
	
	private final M model;
	private final List<Node<M>> childrens;
	
	public Node(M model, List<Node<M>> childrens) {
		this.model = model; //nullable
		this.childrens = childrens == null ? Collections.emptyList() : childrens;
	}
	
	public List<ModelIntInerval<M>> apply(ZonedDateTime start, ZonedDateTime exclusifEnd, int step) {

		return until(start, 0, (int)(start.until(exclusifEnd, SECONDS)/step), step);
	}
	
	protected List<ModelIntInerval<M>> until(ZonedDateTime date, int index, int max, int step) {
		
		List<ModelIntInerval<M>> list = new LinkedList<>();
		long diff = compareTo(date);
		while(diff != 0) {
			int loops = (int) (diff / step);
			if(diff > 0) {
				loops = Math.min(loops, max);
				ModelIntInerval<ZonedDateTime> context = new ModelIntInerval<>(index, loops, date); //final reference
				List<ModelIntInerval<M>> childList = childrens.parallelStream() //parallel ?
						.flatMap(n-> n.until(context.model, context.start, context.exclusifEnd, step).stream())
						.sequential()
						.sorted(ModelIntInerval.COMPARATOR)
						.collect(Collectors.toList());
				if(model != null) {
					if(childList.isEmpty()) {
						list.add(new ModelIntInerval<>(context.start, context.exclusifEnd, model));
					}
					else {
						if(context.start < childList.get(0).getStart()) {
							list.add(new ModelIntInerval<>(context.start, childList.get(0).getStart(), model));
						}
						list.add(childList.get(0));
						for(int i=1; i<childList.size(); i++) {
							if(childList.get(i-1).getExclusifEnd() < childList.get(i).getStart()) {
								list.add(new ModelIntInerval<>(childList.get(i-1).getExclusifEnd(), childList.get(i).getStart(), model));
							}
							list.add(childList.get(i));
						}
						if(childList.get(childList.size()-1).getExclusifEnd() < context.exclusifEnd) {
							list.add(new ModelIntInerval<>(childList.get(childList.size()-1).getExclusifEnd(), context.exclusifEnd, model));
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
