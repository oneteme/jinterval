package org.usf.learn.node;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
final class SinglePartition<M> {
	
	static final Comparator<SinglePartition<?>> PARTITON_COMPARATOR = comparingInt(SinglePartition::getStart);
	
	int start;
	int exclusifEnd;
	final M model;
	
	static final <M> List<SinglePartition<M>> assign(List<SinglePartition<M>> l1, List<SinglePartition<M>> l2) {
		
		int ldx = 0;
		int exclusifEnd;
		List<SinglePartition<M>> res = new LinkedList<>();
		for(int i=0; i<l2.size(); i++) {
			
			exclusifEnd = l2.get(i).start;
			while(ldx < l1.size() && l1.get(ldx).exclusifEnd <= exclusifEnd) {
				res.add(l1.get(ldx++));
			}
			if(ldx < l1.size() && l1.get(ldx).start < exclusifEnd) {
				res.add(new SinglePartition<>(l1.get(ldx).start, exclusifEnd, l1.get(ldx).model));
			}
			res.add(l2.get(i));

			exclusifEnd = l2.get(i).exclusifEnd;
			while(ldx < l1.size() && l1.get(ldx).exclusifEnd <= exclusifEnd) {
				ldx++;
			}
			if(ldx < l1.size() && l1.get(ldx).start <= exclusifEnd) {
				l1.get(ldx).setStart(exclusifEnd);
			}
		}
		for(int i=ldx; i<l1.size(); i++) {
			res.add(l1.get(i));
		}
		return res;
	}
	
}