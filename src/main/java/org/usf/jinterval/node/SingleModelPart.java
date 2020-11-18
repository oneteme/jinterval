package org.usf.jinterval.node;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Setter
@Getter
@AllArgsConstructor
final class SingleModelPart<M> {
	
	static final Comparator<SingleModelPart<?>> PARTITON_COMPARATOR = comparingInt(SingleModelPart::getStart);
	
	int start;
	int exclusifEnd;
	final M model;
	
	@Override
	public String toString() {
		return model + " : [" + start + " - " + exclusifEnd + "]";
	}
	
	static final <M> List<SingleModelPart<M>> assign(List<SingleModelPart<M>> l1, List<SingleModelPart<M>> l2) {
		
		int ldx = 0;
		int exclusifEnd;
		List<SingleModelPart<M>> res = new LinkedList<>();
		for(int i=0; i<l2.size(); i++) {
			
			exclusifEnd = l2.get(i).start;
			while(ldx < l1.size() && l1.get(ldx).exclusifEnd <= exclusifEnd) {
				res.add(l1.get(ldx++));
			}
			if(ldx < l1.size() && l1.get(ldx).start < exclusifEnd) {
				res.add(new SingleModelPart<>(l1.get(ldx).start, exclusifEnd, l1.get(ldx).model));
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