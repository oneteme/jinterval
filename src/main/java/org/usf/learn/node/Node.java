package org.usf.learn.node;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Comparator.comparingInt;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Node<M> {
	
	private final M model;
	private final List<Node<M>> childrens;
	
	public List<ModelIntInerval<M>> apply(ZonedDateTime start, ZonedDateTime exclusifEnd, int step) {
		
		return apply(start, exclusifEnd, step, SECONDS);
	}
	
	public List<ModelIntInerval<M>> apply(ZonedDateTime start, ZonedDateTime exclusifEnd, int step, ChronoUnit unit) {
		
		int loops = (int)(start.until(exclusifEnd, unit) / step);
		return until(start, 0, loops, step, unit);
	}
	
	protected List<ModelIntInerval<M>> until(ZonedDateTime date, int index, int max, int step, ChronoUnit unit) {
		
		List<ModelIntInerval<M>> list = new LinkedList<>();
		long diff = compareTo(date);
		while(diff != 0) {
			int loops = (int) (diff / step);
			if(diff > 0) {
				loops = Math.min(loops, max);
				ModelIntInerval<ZonedDateTime> context = new ModelIntInerval<>(index, loops, date); //final reference
				List<ModelIntInerval<M>> childList = childrens.parallelStream() //parallel ?
						.flatMap(n-> n.until(context.model, context.start, context.exclusifEnd, step, unit).stream())
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
			date = date.plus(diff, unit); //jump to next
			index += loops;
			diff = compareTo(date);
		}
		return list;
	}
	
	protected long compareTo(ZonedDateTime date) {
		
		return Integer.MAX_VALUE;
	}
	
	@Getter
	@RequiredArgsConstructor
	static class ModelIntInerval<M> {
		
		private static final Comparator<ModelIntInerval<?>> COMPARATOR = comparingInt(ModelIntInerval::getStart);
		
		private final int start;
		private final int exclusifEnd;
		private final M model;
	}
	
}
