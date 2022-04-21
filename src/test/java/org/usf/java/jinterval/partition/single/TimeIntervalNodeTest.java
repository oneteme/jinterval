package org.usf.java.jinterval.partition.single;

import static java.time.LocalTime.MIN;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.java.jinterval.Utils.zdt;
import static org.usf.java.jinterval.core.ImmutableInterval.regularInterval;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TimeIntervalNodeTest {

	@Test
	void testAdjustInterval_in() {

		testAdjustInterval(MIN, MIN, zdt(2022, 04, 01, 0, 0),  //fullInterval, begin
				zdt(2022, 04, 01, 0), zdt(2022, 04, 02, 0));

		testAdjustInterval(MIN, MIN, zdt(2022, 04, 01, 13, 0),  //fullInterval, middle
				zdt(2022, 04, 01, 0), zdt(2022, 04, 02, 0));

		testAdjustInterval(MIN, MIN, zdt(2022, 04, 01, 23, 30),  //fullInterval, last
				zdt(2022, 04, 01, 0), zdt(2022, 04, 02, 0));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(13, 0), zdt(2022, 04, 01, 13, 0),  //fullInterval, begin
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 13));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(13, 0), zdt(2022, 04, 01, 17, 45),  //fullInterval, middle
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 13));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(13, 0), zdt(2022, 04, 02, 0),  	//fullInterval, middle loop
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 13));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(13, 0), zdt(2022, 04, 02, 12, 50),  //fullInterval, last
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 13));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(0, 0), zdt(2022, 04, 01, 13, 0),  //invertedInterval, begin
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 0));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(0, 0), zdt(2022, 04, 01, 17, 45),  //invertedInterval, begin
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 0));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(0, 0), zdt(2022, 04, 01, 23, 59),  //invertedInterval, last
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 0));
		
		testAdjustInterval(MIN, LocalTime.of(23, 59), zdt(2022, 04, 01, 0, 0),  //fullInterval, begin
				zdt(2022, 04, 01, 0), zdt(2022, 04, 01, 23, 59));

		testAdjustInterval(MIN, LocalTime.of(23, 59), zdt(2022, 04, 01, 13, 44),  //fullInterval, middle
				zdt(2022, 04, 01, 0), zdt(2022, 04, 01, 23, 59));

		testAdjustInterval(MIN, LocalTime.of(23, 59), zdt(2022, 04, 01, 23, 58),  //fullInterval, middle
				zdt(2022, 04, 01, 0), zdt(2022, 04, 01, 23, 59));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(14, 0), zdt(2022, 04, 01, 13, 0),  //fullInterval, begin
				zdt(2022, 04, 01, 13, 0), zdt(2022, 04, 01, 14, 0));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(14, 0), zdt(2022, 04, 01, 13, 30),  //fullInterval, middle
				zdt(2022, 04, 01, 13, 0), zdt(2022, 04, 01, 14, 0));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(14, 0), zdt(2022, 04, 01, 13, 59),  //fullInterval, middle
				zdt(2022, 04, 01, 13, 0), zdt(2022, 04, 01, 14, 0));
	}
	
	@Test
	void testAdjustInterval_out() {
		
		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(0, 0), zdt(2022, 04, 01, 0, 0),  //invertedInterval, begin
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 0));

		testAdjustInterval(LocalTime.of(13, 0), LocalTime.of(0, 0), zdt(2022, 04, 01, 12, 0),  //invertedInterval, last
				zdt(2022, 04, 01, 13), zdt(2022, 04, 02, 0));
		
		testAdjustInterval(LocalTime.of(22, 0), LocalTime.of(10, 0), zdt(2022, 04, 01, 10, 0),  //invertedInterval, begin
				zdt(2022, 04, 01, 22), zdt(2022, 04, 02, 10));
		
		testAdjustInterval(LocalTime.of(0, 0), LocalTime.of(13, 0), zdt(2022, 04, 01, 13, 0),  //invertedInterval, after
				zdt(2022, 04, 02, 0), zdt(2022, 04, 02, 13));

		testAdjustInterval(LocalTime.of(0, 0), LocalTime.of(13, 0), zdt(2022, 04, 01, 23, 59),  //invertedInterval, after
				zdt(2022, 04, 02, 0), zdt(2022, 04, 02, 13));
		
		testAdjustInterval(LocalTime.of(10, 0), LocalTime.of(22, 0), zdt(2022, 04, 01, 0),  //invertedInterval, after
				zdt(2022, 04, 01, 10), zdt(2022, 04, 01, 22));

		testAdjustInterval(LocalTime.of(10, 0), LocalTime.of(22, 0), zdt(2022, 04, 01, 9, 59),  //invertedInterval, after
				zdt(2022, 04, 01, 10), zdt(2022, 04, 01, 22));
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
    						new SingleModelPart<>(0, 24, "HOURS")),
    				Arrays.asList(
    						new TimeIntervalNode<>("HOURS", LocalTime.MIN, LocalTime.MIN, null))),

    		Arguments.of(
    				expected("2019-12-31T11:00:00Z", "2019-12-31T14:00:00Z", 3600),
    				Arrays.asList(
    						new TimeIntervalNode<>("1", LocalTime.of(8, 00), LocalTime.of(12, 00), null),
							new TimeIntervalNode<>("2", LocalTime.of(15, 00), LocalTime.of(18, 00), null))),

    		Arguments.of(
    				expected("2019-12-31T10:45:00Z", "2019-12-31T14:15:00Z", 900, 
    						new SingleModelPart<>(0, 1, "1"), new SingleModelPart<>(13, 14, "2")),
    				Arrays.asList(
    						new TimeIntervalNode<>("1", LocalTime.of(8, 00), LocalTime.of(12, 00), null),
							new TimeIntervalNode<>("2", LocalTime.of(15, 00), LocalTime.of(18, 00), null))),
	    		
    		Arguments.of(
    				expected("2020-01-01T23:00:00Z", "2020-01-02T23:00:00Z", 1800,
    						new SingleModelPart<>(0, 24, "AM"), new SingleModelPart<>(24, 48, "PM")),
    				Arrays.asList(
    						new TimeIntervalNode<>("AM", LocalTime.MIN, LocalTime.NOON, null), 
    						new TimeIntervalNode<>("PM", LocalTime.NOON, LocalTime.MIN, null))),
    		
    		Arguments.of(
				expected("2020-01-01T00:00:00Z", "2020-01-02T00:00:00Z", 600,
						new SingleModelPart<>(0, 144, "ELSE"), 
						new SingleModelPart<>(0, 66, "AM"), new SingleModelPart<>(66, 138, "PM"), new SingleModelPart<>(138, 144, "AM")),
				Arrays.asList(
						new TimeIntervalNode<>("ELSE", LocalTime.MIN, LocalTime.MIN, null),
						new TimeIntervalNode<>("AM", LocalTime.MIN, LocalTime.NOON, null), 
						new TimeIntervalNode<>("PM", LocalTime.NOON, LocalTime.MIN, null))),

    		Arguments.of(
    				expected("2020-01-01T23:00:00Z", "2020-01-02T23:00:00Z", 60,
    						new SingleModelPart<>(0, 420, "AM"), new SingleModelPart<>(420, 585, "AMP"), 
    						new SingleModelPart<>(585, 700, "AM"), new SingleModelPart<>(700, 720, "AMP"),
    						
    						new SingleModelPart<>(720, 837, "PMP"), new SingleModelPart<>(837, 1053, "PM"), 
    						new SingleModelPart<>(1053, 1140, "PMP"), new SingleModelPart<>(1140, 1440, "PM")
    					),
    				Arrays.asList(
    						new TimeIntervalNode<>("AM", LocalTime.MIN, LocalTime.NOON, Arrays.asList(
    	    						new TimeIntervalNode<>("AMP", LocalTime.of( 7,00), LocalTime.of(9, 45), null), 
    	    						new TimeIntervalNode<>("AMP", LocalTime.of(11,40), LocalTime.of(12, 00), null))), 
    						new TimeIntervalNode<>("PM", LocalTime.NOON, LocalTime.MIN, Arrays.asList(
    	    						new TimeIntervalNode<>("PMP", LocalTime.of(12,00), LocalTime.of(13, 57), null), 
    	    						new TimeIntervalNode<>("PMP", LocalTime.of(17,33), LocalTime.of(19, 00), null)))))
    		
    		
    		
    		//TODO hour change case
    		
    		);
	    
	}
	

	private static void testAdjustInterval(LocalTime start, LocalTime endExclusive, ZonedDateTime zdt, ZonedDateTime es, ZonedDateTime ee) {
		
		var in = new TimeIntervalNode<String>("", start, endExclusive).adjustInterval(zdt);
		assertEquals(in, regularInterval(es, ee));
	}
	
    				
	
	@SafeVarargs
	static <T> SingleModelPartition<T> expected(String start, String exclusifEnd, int step, SingleModelPart<T> ... parts) {
		
		return new SingleModelPartition<>(Instant.parse(start), Instant.parse(exclusifEnd), step, Arrays.asList(parts));
	}
	
	
	
}
