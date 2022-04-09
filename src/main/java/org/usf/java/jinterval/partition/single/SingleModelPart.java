package org.usf.java.jinterval.partition.single;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.usf.java.jinterval.core.IntervalUtils;
import org.usf.java.jinterval.partition.Part;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author u$f
 *
 */
@EqualsAndHashCode
@Setter(value = AccessLevel.PACKAGE)
@Getter
@AllArgsConstructor
final class SingleModelPart<M> implements Part<M> {
	
	static final Comparator<SingleModelPart<?>> PARTITON_COMPARATOR = comparingInt(SingleModelPart::getStartIndex);
	
	int startIndex;
	int endExclusiveIndex;
	final M model;
	
	@Override
	public String toString() {
		return model + " : " + IntervalUtils.toString(startIndex, endExclusiveIndex);
	}
	
	static final <M> List<SingleModelPart<M>> assign(List<SingleModelPart<M>> l1, List<SingleModelPart<M>> l2) {
		
		var ldx = 0;
		int exclusifEnd;
		List<SingleModelPart<M>> res = new LinkedList<>();
		for(var i=0; i<l2.size(); i++) {
			
			exclusifEnd = l2.get(i).startIndex;
			while(ldx < l1.size() && l1.get(ldx).endExclusiveIndex <= exclusifEnd) {
				res.add(l1.get(ldx++));
			}
			if(ldx < l1.size() && l1.get(ldx).startIndex < exclusifEnd) {
				res.add(new SingleModelPart<>(l1.get(ldx).startIndex, exclusifEnd, l1.get(ldx).model));
			}
			res.add(l2.get(i));

			exclusifEnd = l2.get(i).endExclusiveIndex;
			while(ldx < l1.size() && l1.get(ldx).endExclusiveIndex <= exclusifEnd) {
				ldx++;
			}
			if(ldx < l1.size() && l1.get(ldx).startIndex <= exclusifEnd) {
				l1.get(ldx).setStartIndex(exclusifEnd);
			}
		}
		for(int i=ldx; i<l1.size(); i++) {
			res.add(l1.get(i));
		}
		return res;
	}
	
}