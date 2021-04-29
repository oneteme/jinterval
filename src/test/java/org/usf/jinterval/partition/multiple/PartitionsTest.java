package org.usf.jinterval.partition.multiple;

import static java.time.LocalTime.of;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.usf.jinterval.core.ImmutableInterval;

class PartitionsTest {
	
	@Test
	void testIntervalPartition() {
		
		assertTrue(Partitions.intervalPartition(Collections.emptyList()).getPartitions().isEmpty());

		var arr1 = Arrays.asList(new ImmutableInterval<>(1, 5));
		var p1 = Partitions.intervalPartition(arr1).getPartitions();
		assertEquals(1, p1.size());
		assertPart(p1.get(0), 0, 1, 1, 5, arr1);

		var arr2 = Arrays.asList(new ImmutableInterval<>(1, 5), new ImmutableInterval<>(1, 5));
		var p2 = Partitions.intervalPartition(arr2).getPartitions();
		assertEquals(1, p2.size());
		assertPart(p2.get(0), 0, 1, 1, 5, arr2);

		var arr3 = Arrays.asList(new ImmutableInterval<>(5, 8), new ImmutableInterval<>(1, 10));
		var p3 = Partitions.intervalPartition(arr3).getPartitions();
		assertEquals(3, p3.size());
		assertPart(p3.get(0), 0, 1, 1, 5, Arrays.asList(arr3.get(1)));
		assertPart(p3.get(1), 1, 2, 5, 8, Arrays.asList(arr3.get(0), arr3.get(1)));
		assertPart(p3.get(2), 2, 3, 8, 10, Arrays.asList(arr3.get(1)));
		
		var arr4 = Arrays.asList(new ImmutableInterval<>(8, 5), new ImmutableInterval<>(1, 10));
		var p4 = Partitions.intervalPartition(arr4).getPartitions();
		assertEquals(4, p4.size());
		assertPart(p4.get(0), 0, 1, 1, 5, arr4);
		assertPart(p4.get(1), 1, 2, 5, 8, Arrays.asList(arr4.get(1)));
		assertPart(p4.get(2), 2, 3, 8, 10, arr4);
		assertPart(p4.get(3), 3, 4, 10, 1, Arrays.asList(arr4.get(0)));

		var arr5 = Arrays.asList(new ImmutableInterval<>(18, 5), new ImmutableInterval<>(2, -10));
		var p5 = Partitions.intervalPartition(arr5).getPartitions();
		assertEquals(4, p5.size());
		assertPart(p5.get(0), 0, 1, -10, 2, Arrays.asList(arr5.get(0)));
		assertPart(p5.get(1), 1, 2, 2, 5, arr5);
		assertPart(p5.get(2), 2, 3, 5, 18, Arrays.asList(arr5.get(1)));
		assertPart(p5.get(3), 3, 4, 18, -10, arr5);
	}
	
	@Test
	void testIntervalPartition2() {

		var arr1 = Arrays.asList(new ImmutableInterval<>(of(9, 0), of(19, 0)));
		var p1 = Partitions.intervalPartition(arr1, of(14, 0), of(17, 0)).getPartitions();
		assertEquals(3, p1.size());
		assertPart(p1.get(0), 0, 1, of(9, 0), of(14, 0), arr1);
		assertPart(p1.get(1), 1, 2, of(14, 0), of(17, 0), arr1);
		assertPart(p1.get(2), 2, 3, of(17, 0), of(19, 0), arr1);

		var arr2 = Arrays.asList(new ImmutableInterval<>(of(9, 0), of(19, 0)), new ImmutableInterval<>(of(14, 0), of(22, 0)));
		var p2 = Partitions.intervalPartition(arr2, of(0, 0), of(23, 0)).getPartitions();
		assertEquals(5, p2.size());
		assertPart(p2.get(0), 0, 1, of(0, 0), of(9, 0), Collections.emptyList());
		assertPart(p2.get(1), 1, 2, of(9, 0), of(14, 0), Arrays.asList(arr2.get(0)));
		assertPart(p2.get(2), 2, 3, of(14, 0), of(19, 0), arr2);
		assertPart(p2.get(3), 3, 4, of(19, 0), of(22, 0), Arrays.asList(arr2.get(1)));
		assertPart(p2.get(4), 4, 5, of(22, 0), of(23, 0), Collections.emptyList());
		
		var arr3 = Arrays.asList(new ImmutableInterval<>(of(9, 0), of(19, 0)), new ImmutableInterval<>(of(14, 0), of(22, 0)));
		var p3 = Partitions.intervalPartition(arr3, of(2, 0), of(2, 0)).getPartitions();
		assertEquals(5, p3.size());
		assertPart(p3.get(0), 0, 1, of(2, 0), of(9, 0), Collections.emptyList());
		assertPart(p3.get(1), 1, 2, of(9, 0), of(14, 0), Arrays.asList(arr3.get(0)));
		assertPart(p3.get(2), 2, 3, of(14, 0), of(19, 0), arr3);
		assertPart(p3.get(3), 3, 4, of(19, 0), of(22, 0), Arrays.asList(arr3.get(1)));
		assertPart(p3.get(4), 4, 5, of(22, 0), of(2, 0), Collections.emptyList());
		
		var arr4 = Arrays.asList(new ImmutableInterval<>(of(19, 0), of(9, 0)), new ImmutableInterval<>(of(14, 0), of(22, 0)));
		var p4 = Partitions.intervalPartition(arr4, of(0, 0), of(0, 0)).getPartitions();
		assertEquals(5, p4.size());
		assertPart(p4.get(0), 0, 1, of(0, 0), of(9, 0), Arrays.asList(arr4.get(0)));
		assertPart(p4.get(1), 1, 2, of(9, 0), of(14, 0), Collections.emptyList());
		assertPart(p4.get(2), 2, 3, of(14, 0), of(19, 0), Arrays.asList(arr4.get(1)));
		assertPart(p4.get(3), 3, 4, of(19, 0), of(22, 0), arr4);
		assertPart(p4.get(4), 4, 5, of(22, 0), of(0, 0), Arrays.asList(arr4.get(0)));

		var arr5 = Arrays.asList(new ImmutableInterval<>(of(19, 0), of(9, 0)), new ImmutableInterval<>(of(14, 0), of(22, 0)));
		var ex = assertThrows(IllegalArgumentException.class, ()-> Partitions.intervalPartition(arr5, of(0, 0), of(23, 59, 59)));
		assertEquals("[00:00, 23:59:59[ can't be regular", ex.getMessage());
	}
	
	private static <T> void assertPart(MultiModelPart<T> part, int startIndex, int exclusifEndIndex, Object start, Object end, List<T> data) {
		
		assertEquals(startIndex, part.getStartIndex());
		assertEquals(exclusifEndIndex, part.getExclusifEndIndex());
		assertEquals(start, part.getStart());
		assertEquals(end, part.getExclusifEnd());
		assertArrayEquals(data.toArray(), part.getModel().toArray());
	}

}
