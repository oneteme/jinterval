package org.usf.java.jinterval.partition.single;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.java.jinterval.Utils.zdt;
import static org.usf.java.jinterval.core.ImmutableInterval.regularInterval;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DayOfWeekIntervalNodeTest {
	
	@Test
	void testAdjustInterval_prv() {
		
		testAdjustInterval(MONDAY, MONDAY, zdt(2022, 03, 28, 0),  //fullInterval, begin
				zdt(2022, 03, 28, 0), zdt(2022, 04, 04, 0));

		testAdjustInterval(MONDAY, MONDAY, zdt(2022, 04, 01, 12),  //fullInterval, middle
				zdt(2022, 03, 28, 0), zdt(2022, 04, 04, 0));

		testAdjustInterval(MONDAY, MONDAY, zdt(2022, 04, 03, 23),  //fullInterval, last
				zdt(2022, 03, 28, 0), zdt(2022, 04, 04, 0));

		testAdjustInterval(MONDAY, SUNDAY, zdt(2022, 03, 28, 0),  //regularInterval, begin
				zdt(2022, 03, 28, 0), zdt(2022, 04, 03, 0));

		testAdjustInterval(MONDAY, SUNDAY, zdt(2022, 04, 01, 12),  //regularInterval, middle
				zdt(2022, 03, 28, 0), zdt(2022, 04, 03, 0));

		testAdjustInterval(MONDAY, SUNDAY, zdt(2022, 04, 02, 23),  //regularInterval, last
				zdt(2022, 03, 28, 0), zdt(2022, 04, 03, 0));
		
		testAdjustInterval(FRIDAY, THURSDAY, zdt(2022, 04, 01, 0),  //invertedInterval, begin
				zdt(2022, 04, 01, 0), zdt(2022, 04, 07, 0));

		testAdjustInterval(FRIDAY, THURSDAY, zdt(2022, 04, 03, 12),  //invertedInterval, middle
				zdt(2022, 04, 01, 0), zdt(2022, 04, 07, 0));
		
		testAdjustInterval(FRIDAY, THURSDAY, zdt(2022, 04, 04, 05),  //invertedInterval, middle+loop
				zdt(2022, 04, 01, 0), zdt(2022, 04, 07, 0));

		testAdjustInterval(FRIDAY, THURSDAY, zdt(2022, 04, 06, 23),  //invertedInterval, last
				zdt(2022, 04, 01, 0), zdt(2022, 04, 07, 0));
	}
	

	@Test
	void testAdjustInterval_nxt() {

		testAdjustInterval(MONDAY, SUNDAY, zdt(2022, 03, 27, 18),  //regularInterval,
				zdt(2022, 03, 28, 0), zdt(2022, 04, 03, 0));
		
		testAdjustInterval(TUESDAY, SATURDAY, zdt(2022, 04, 04, 0),  //regularInterval, before
				zdt(2022, 04, 05, 0), zdt(2022, 04, 9, 0));
		
		testAdjustInterval(TUESDAY, SATURDAY, zdt(2022, 04, 02, 0),  //regularInterval, after
				zdt(2022, 04, 05, 0), zdt(2022, 04, 9, 0));

		testAdjustInterval(SATURDAY, TUESDAY, zdt(2022, 03, 30, 0),  //invertedInterval, before
				zdt(2022, 04, 02, 0), zdt(2022, 04, 05, 0));
		
		testAdjustInterval(SATURDAY, TUESDAY, zdt(2022, 04, 01, 23),  //invertedInterval, after
				zdt(2022, 04, 02, 0), zdt(2022, 04, 05, 0));

	}
	

	@ParameterizedTest(name = "{1})")
	@MethodSource("caseFactory")
	<M> void test(SingleModelPartition<M> expected, List<Node<String>> nodes) {

		SingleModelPartition<String> res = new Node<String>(null, nodes).partitions(expected.getStart().atZone(ZoneId.systemDefault()), expected.getEndExclusive().atZone(ZoneId.systemDefault()), expected.getStep());
		assertEquals(expected.getStart(), res.getStart());
		assertEquals(expected.getEndExclusive(), res.getEndExclusive());
		assertEquals(expected.getStep(), res.getStep());
		assertArrayEquals(expected.getPartitions().toArray(), res.getPartitions().toArray());
	}
	
	private static Stream<Arguments> caseFactory() {
	    return Stream.of(

    		Arguments.of(
    				expected("2019-12-31T23:00:00Z", "2020-01-01T23:00:00Z", 3600,
    						new SingleModelPart<>(0, 24, "FULL WEEK")),
    				Arrays.asList(
    						new DayOfWeekIntervalNode<>("FULL WEEK", MONDAY, MONDAY, null))),
    		
			Arguments.of(
					expected("2019-12-31T23:00:00Z", "2020-01-01T23:00:00Z", 1800,
							new SingleModelPart<>(0, 48, "FULL WEEK")),
					Arrays.asList(
							new DayOfWeekIntervalNode<>("FULL WEEK", FRIDAY, FRIDAY, null))),
			
    		Arguments.of(
    				expected("2019-12-31T23:00:00Z", "2020-01-01T23:00:00Z", 3600),
    				Arrays.asList(
    						new DayOfWeekIntervalNode<>("!WEDNESDAY", THURSDAY, WEDNESDAY, null))),

    		Arguments.of(
    				expected("2020-01-01T17:00:00Z", "2020-01-02T22:00:00Z", 600, 
							new SingleModelPart<>(0, 36, "MODEL")),
    				Arrays.asList(
    						new DayOfWeekIntervalNode<>("MODEL", FRIDAY, THURSDAY, null))),

    		Arguments.of(
    				expected("2020-01-01T17:00:00Z", "2020-01-05T22:00:00Z", 600, 
							new SingleModelPart<>(0, 36, "MODEL"), new SingleModelPart<>(180, 606, "MODEL")),
    				Arrays.asList(
    						new DayOfWeekIntervalNode<>("MODEL", FRIDAY, THURSDAY, null)))
			);
	    
	}
	
	private static void testAdjustInterval(DayOfWeek start, DayOfWeek endExclusive, ZonedDateTime zdt, ZonedDateTime es, ZonedDateTime ee) {
		
		var in = new DayOfWeekIntervalNode<String>("", start, endExclusive).adjustInterval(zdt);
		assertEquals(in, regularInterval(es, ee));
	}
	
	@SafeVarargs
	static <T> SingleModelPartition<T> expected(String start, String exclusifEnd, int step, SingleModelPart<T> ... parts) {
		
		return new SingleModelPartition<>(Instant.parse(start), Instant.parse(exclusifEnd), step, Arrays.asList(parts));
	}
	
		
}
