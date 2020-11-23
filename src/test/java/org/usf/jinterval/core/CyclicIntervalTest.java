package org.usf.jinterval.core;

import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.provider.Arguments;

@Disabled
public class CyclicIntervalTest extends RegularIntervalTest {

	static Stream<Arguments> numberIntervals() {
	    return convert(RegularIntervalTest.numberIntervals());
	}

	static Stream<Arguments> periodIntervals() {

	    return convert(RegularIntervalTest.periodIntervals());
	}

	@SuppressWarnings("unchecked")
	private static <T extends Comparable<? super T>> Stream<Arguments> convert(Stream<Arguments> s){
		return s.peek(a-> {
			RegularIntervalImpl<T> o = (RegularIntervalImpl<T>) a.get()[0];
			a.get()[0] = new CyclicIntervalImpl<>(o.getStart(), o.getExclusifEnd(), o.getFn());
		});
	}
}
