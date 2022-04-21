package org.usf.java.jinterval.partition.single;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.java.jinterval.Utils.zdt;
import static org.usf.java.jinterval.partition.single.RegularIntervalNode.ofLocal;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class RegularIntervalNodeTest {
	
	@Test
	void testAdjustInterval_localDate() {
		
		var n = ofLocal("", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1), null);

		assertPartition(n, zdt(2021, 1, 1, 0), zdt(2022, 1, 1, 0), 1800, emptyList()); //before
		assertPartition(n, zdt(2022, 2, 1, 0), zdt(2023, 1, 1, 0), 1800, emptyList()); //after

		assertPartition(n, zdt(2022, 1, 1, 0), zdt(2022, 2, 1, 0), 1800, asList(new SingleModelPart<>(0, 1488, ""))); //equals
		assertPartition(n, zdt(2022, 1, 1, 0, 30), zdt(2022, 1, 31, 23, 59), 1800, asList(new SingleModelPart<>(0, 1486, ""))); //full in
		assertPartition(n, zdt(2022, 1, 10, 22), zdt(2022, 1, 15, 22), 1800, asList(new SingleModelPart<>(0, 240, ""))); //full in

		assertPartition(n, zdt(2021, 12, 1, 0), zdt(2022, 3, 1, 1), 1800, asList(new SingleModelPart<>(1488, 2976, ""))); //intersect
		assertPartition(n, zdt(2021, 1, 1, 0), zdt(2022, 1, 1, 1), 1800, asList(new SingleModelPart<>(17520, 17522, ""))); //intersect
		assertPartition(n, zdt(2022, 1, 15, 22), zdt(2022, 2, 15, 1), 1800, asList(new SingleModelPart<>(0, 772, "")));  //intersect
	}
	
	private final void assertPartition(Node<?> n, ZonedDateTime es, ZonedDateTime ee, int step, 
			Collection<SingleModelPart<?>> partitions){
		
		var p = n.partitions(es, ee, step, null);
		assertEquals(es.toInstant(), p.getStart());
		assertEquals(ee.toInstant(), p.getEndExclusive());
		assertEquals(step, p.getStep());
		assertArrayEquals(partitions.toArray(), p.getPartitions().toArray());
	}

}
