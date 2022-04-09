package org.usf.java.jinterval.partition.multiple;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.java.jinterval.Utils.assertEmpty;
import static org.usf.java.jinterval.Utils.assertExceptionMsg;
import static org.usf.java.jinterval.Utils.intervals;
import static org.usf.java.jinterval.partition.multiple.Partitions.intervalPartition;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.usf.java.jinterval.Utils;
import org.usf.java.jinterval.core.ImmutableInterval;
import org.usf.java.jinterval.partition.multiple.MultiModelPart;
import org.usf.java.jinterval.partition.multiple.Partitions;

class PartitionsTest {
	
	@Test
	void testIntervalParts_KO(){
		assertExceptionMsg(IllegalArgumentException.class, ()-> intervalPartition(false, intervals(1, 1)), "start >= exclusifEnd");
		assertExceptionMsg(IllegalArgumentException.class, ()-> intervalPartition(false, intervals(3, 7, 15, 10)), "start >= exclusifEnd");
		assertExceptionMsg(IllegalArgumentException.class, ()-> intervalPartition(false, emptyList(), 1, 1), "start >= exclusifEnd");
		assertExceptionMsg(IllegalArgumentException.class, ()-> intervalPartition(false, intervals(1, 10), 15, 10), "start >= exclusifEnd");
	}

	@Test
	void testIntervalParts_empty(){
		assertEmpty(intervalPartition(true, emptyList(), null, null).getPartitions());
		assertEmpty(intervalPartition(false, emptyList(), null, null).getPartitions());
		assertEmpty(intervalPartition(true, emptyList(), 15, null).getPartitions());
		assertEmpty(intervalPartition(false, emptyList(), null, 10).getPartitions());
	
		assertPartEquals(intervalPartition(true, emptyList(), 15, 10).getPartitions(),
				new MultiModelPart<>(emptyList(), 0, 1, 15, 10));
		assertPartEquals(intervalPartition(false, emptyList(), 10, 15).getPartitions(),
				new MultiModelPart<>(emptyList(), 0, 1, 10, 15));
	}

	@Test
	void testIntervalParts_regular(){
		List<ImmutableInterval<Integer>> intervals = intervals(3,7, 10,15, 8,20);
		assertPartEquals(intervalPartition(false, intervals).getPartitions(),
				new MultiModelPart<>(intervals(3,7), 0, 1, 3, 7),
				new MultiModelPart<>(emptyList(), 1, 2, 7, 8),
				new MultiModelPart<>(intervals(8,20), 2, 3, 8, 10),
				new MultiModelPart<>(intervals(10,15, 8,20), 3, 4, 10, 15),
				new MultiModelPart<>(intervals(8,20), 4, 5, 15, 20));
		assertPartEquals(intervalPartition(false, intervals, 14, null).getPartitions(),
				new MultiModelPart<>(intervals(10,15, 8,20), 0, 1, 14, 15),
				new MultiModelPart<>(intervals(8,20), 1, 2, 15, 20));
		assertPartEquals(intervalPartition(false, intervals, null, 10).getPartitions(),
				new MultiModelPart<>(intervals(3,7), 0, 1, 3, 7),
				new MultiModelPart<>(emptyList(), 1, 2, 7, 8),
				new MultiModelPart<>(intervals(8,20), 2, 3, 8, 10));
		assertPartEquals(intervalPartition(false, intervals, 9, 30).getPartitions(),
				new MultiModelPart<>(intervals(8,20), 0, 1, 9, 10),
				new MultiModelPart<>(intervals(10,15, 8, 20), 1, 2, 10, 15),
				new MultiModelPart<>(intervals(8,20), 2, 3, 15, 20),
				new MultiModelPart<>(emptyList(), 3, 4, 20, 30));
	}
	
	@Test
	void testIntervalParts_inverted(){
		List<ImmutableInterval<Integer>> intervals = intervals(3, 7, 15, 1, 8, 20);
		assertPartEquals(intervalPartition(true, intervals).getPartitions(),
				new MultiModelPart<>(emptyList(), 0, 1, 1, 3),
				new MultiModelPart<>(intervals(3,7), 1, 2, 3, 7),
				new MultiModelPart<>(emptyList(), 2, 3, 7, 8),
				new MultiModelPart<>(intervals(8,20), 3, 4, 8, 15),
				new MultiModelPart<>(intervals(15,1, 8, 20), 4, 5, 15, 20),
				new MultiModelPart<>(intervals(15,1), 5, 6, 20, 1));
		assertPartEquals(intervalPartition(true, intervals, 7, null).getPartitions(),
				new MultiModelPart<>(emptyList(), 0, 1, 7, 8),
				new MultiModelPart<>(intervals(8,20), 1, 2, 8, 15),
				new MultiModelPart<>(intervals(15,1, 8,20), 2, 3, 15, 20));
		assertPartEquals(intervalPartition(true, intervals, null, 12).getPartitions(),
				new MultiModelPart<>(emptyList(), 0, 1, 1, 3),
				new MultiModelPart<>(intervals(3,7), 1, 2, 3, 7),
				new MultiModelPart<>(emptyList(), 2, 3, 7, 8),
				new MultiModelPart<>(intervals(8,20), 3, 4, 8, 12));
		assertPartEquals(intervalPartition(true, intervals, 7, 12).getPartitions(),
				new MultiModelPart<>(emptyList(), 0, 1, 7, 8),
				new MultiModelPart<>(intervals(8,20), 1, 2, 8, 12));
		assertPartEquals(intervalPartition(true, intervals, 12, 7).getPartitions(),
				new MultiModelPart<>(intervals(8,20), 0, 1, 12, 15),
				new MultiModelPart<>(intervals(15,1, 8,20), 1, 2, 15, 20),
				new MultiModelPart<>(intervals(15,1), 2, 3, 20, 1),
				new MultiModelPart<>(emptyList(), 3, 4, 1, 3),
				new MultiModelPart<>(intervals(3,7), 4, 5, 3, 7));
	}
	
	@Test
	void periodPartition_regular(){
		
		var d1 = LocalDate.of(2020, 1, 1);
		var d2 = LocalDate.of(2020, 1, 31);
		var d3 = LocalDate.of(2020, 1, 25);
		var d4 = LocalDate.of(2020, 2, 11);
		var d5 = LocalDate.of(2020, 2, 15);
		var d6 = LocalDate.of(2020, 3, 1);

		List<ImmutableInterval<LocalDate>> intervals = intervals(d1,d2, d3,d4, d5,d6);

		assertPartEquals(Partitions.periodPartition(intervals, 1, DAYS).getPartitions(),
				new MultiModelPart<>(intervals(d1,d2), 0, 24, d1, d3),
				new MultiModelPart<>(intervals(d1,d2, d3,d4), 24, 30, d3, d2),
				new MultiModelPart<>(intervals(d3,d4), 30, 41, d2, d4),
				new MultiModelPart<>(emptyList(), 41, 45, d4, d5),
				new MultiModelPart<>(intervals(d5,d6), 45, 60, d5, d6));

		var start = LocalDate.of(2020, 2, 1);
		assertPartEquals(Partitions.periodPartition(intervals, start, null, 1, DAYS).getPartitions(),
				new MultiModelPart<>(intervals(d3,d4), 0, 10, start, d4),
				new MultiModelPart<>(emptyList(), 10, 14, d4, d5),
				new MultiModelPart<>(intervals(d5,d6), 14, 29, d5, d6));

		var end = LocalDate.of(2020, 1, 10);
		assertPartEquals(Partitions.periodPartition(intervals, null, end, 1, DAYS).getPartitions(),
				new MultiModelPart<>(intervals(d1,d2), 0, 9, d1, end));

		start = LocalDate.of(2019, 12, 27);
		end = LocalDate.of(2020, 3, 3);
		assertPartEquals(Partitions.periodPartition(intervals, start, end, 1, DAYS).getPartitions(),
				new MultiModelPart<>(emptyList(), 0, 5, start, d1),
				new MultiModelPart<>(intervals(d1,d2), 5, 29, d1, d3),
				new MultiModelPart<>(intervals(d1,d2, d3,d4), 29, 35, d3, d2),
				new MultiModelPart<>(intervals(d3,d4), 35, 46, d2, d4),
				new MultiModelPart<>(emptyList(), 46, 50, d4, d5),
				new MultiModelPart<>(intervals(d5,d6), 50, 65, d5, d6),
				new MultiModelPart<>(emptyList(), 65, 67, d6, end));
	}

	@Test
	void periodPartition_regular2(){
		
		var d1 = LocalDate.of(2020, 1, 1);
		var d2 = LocalDate.of(2020, 1, 31);
		var d3 = LocalDate.of(2020, 1, 25);
		var d4 = LocalDate.of(2020, 2, 11);
		var d5 = LocalDate.of(2020, 2, 15);
		var d6 = LocalDate.of(2020, 3, 1);

		Function<LocalDate, Instant> fn = date-> date.atStartOfDay(ZoneId.of("UTC")).toInstant();
		List<ImmutableInterval<LocalDate>> intervals = intervals(d1,d2, d3,d4, d5,d6);

		int minutes = 24 * 60 / 30;
		assertPartEquals(Partitions.periodPartition(intervals, 30, MINUTES, fn).getPartitions(),
				new MultiModelPart<>(intervals(d1,d2), 0, 24*minutes, d1, d3),
				new MultiModelPart<>(intervals(d1,d2, d3,d4), 24*minutes, 30*minutes, d3, d2),
				new MultiModelPart<>(intervals(d3,d4), 30*minutes, 41*minutes, d2, d4),
				new MultiModelPart<>(emptyList(), 41*minutes, 45*minutes, d4, d5),
				new MultiModelPart<>(intervals(d5,d6), 45*minutes, 60*minutes, d5, d6));

		int seconds = 24 * 60 * 60 / 10;
		var start = LocalDate.of(2020, 2, 1);
		assertPartEquals(Partitions.periodPartition(intervals, start, null, 10, SECONDS, fn).getPartitions(),
				new MultiModelPart<>(intervals(d3,d4), 0*seconds, 10*seconds, start, d4),
				new MultiModelPart<>(emptyList(), 10*seconds, 14*seconds, d4, d5),
				new MultiModelPart<>(intervals(d5,d6), 14*seconds, 29*seconds, d5, d6));

		var end = LocalDate.of(2020, 1, 10);
		assertPartEquals(Partitions.periodPartition(intervals, null, end, 10, SECONDS, fn).getPartitions(),
				new MultiModelPart<>(intervals(d1,d2), 0*seconds, 9*seconds, d1, end));

		int hours = 24 / 4;
		start = LocalDate.of(2019, 12, 27);
		end = LocalDate.of(2020, 3, 3);
		assertPartEquals(Partitions.periodPartition(intervals, start, end, 4, HOURS, fn).getPartitions(),
				new MultiModelPart<>(emptyList(), 0*hours, 5*hours, start, d1),
				new MultiModelPart<>(intervals(d1,d2), 5*hours, 29*hours, d1, d3),
				new MultiModelPart<>(intervals(d1,d2, d3,d4), 29*hours, 35*hours, d3, d2),
				new MultiModelPart<>(intervals(d3,d4), 35*hours, 46*hours, d2, d4),
				new MultiModelPart<>(emptyList(), 46*hours, 50*hours, d4, d5),
				new MultiModelPart<>(intervals(d5,d6), 50*hours, 65*hours, d5, d6),
				new MultiModelPart<>(emptyList(), 65*hours, 67*hours, d6, end));
	}
	
	
	private static void assertPartEquals(List<? extends MultiModelPart<?>> actual, MultiModelPart<?>...expected) {
		
		assertEquals(expected.length, actual.size());
		for(int i=0; i<expected.length; i++) {
			assertEquals(expected[i].getStartIndex(), actual.get(i).getStartIndex());
			assertEquals(expected[i].getEndExclusiveIndex(), actual.get(i).getEndExclusiveIndex());
			assertEquals(expected[i].getStart(), actual.get(i).getStart());
			assertEquals(expected[i].getExclusifEnd(), actual.get(i).getExclusifEnd());
			assertArrayEquals(expected[i].getModel().toArray(), actual.get(i).getModel().toArray());
		}
	}

}
