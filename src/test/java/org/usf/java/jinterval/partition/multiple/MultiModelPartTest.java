package org.usf.java.jinterval.partition.multiple;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.usf.java.jinterval.partition.multiple.MultiModelPart;

class MultiModelPartTest {

	@Test
	void testReduceBinaryOperator() {

		MultiModelPart<Integer> part = new MultiModelPart<>(emptyList(), 0, 1, null, null);
		assertEquals(Optional.empty(), part.reduce((v1, v2)-> v1+v2));

		part = new MultiModelPart<>(Arrays.asList(1,2,3), 0, 1, null, null);
		assertEquals(-4, part.reduce((v1, v2)-> v1-v2).get());
	}
	

	@Test
	void testReduceIdentity() {

		MultiModelPart<Integer> part = new MultiModelPart<>(emptyList(), 0, 1, null, null);
		assertEquals("", part.reduce("", (v1, v2)-> v1+""+v2));

		part = new MultiModelPart<>(Arrays.asList(1,2,3), 0, 1, null, null);
		assertEquals("0,1,2,3", part.reduce("0", (v1, v2)-> v1+","+v2));
	}

}
