package org.usf.java.jinterval.partition.single;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.usf.java.jinterval.partition.single.Node;
import org.usf.java.jinterval.partition.single.SingleModelPart;
import org.usf.java.jinterval.partition.single.SingleModelPartition;
import org.usf.java.jinterval.partition.single.TimeIntervalNode;

class TimeIntervalNodeTest {
	

	@ParameterizedTest(name = "{1})")
	@MethodSource("caseFactory")
	<M> void test(SingleModelPartition<M> expected, List<Node<String>> nodes) {

		SingleModelPartition<String> res = new Node<String>(null, nodes).partitions(expected.getStart().atZone(ZoneId.systemDefault()), expected.getExclusifEnd().atZone(ZoneId.systemDefault()), expected.getStep());
		assertEquals(expected.getStart(), res.getStart());
		assertEquals(expected.getExclusifEnd(), res.getExclusifEnd());
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
    				
	
	@SafeVarargs
	static <T> SingleModelPartition<T> expected(String start, String exclusifEnd, int step, SingleModelPart<T> ... parts) {
		
		return new SingleModelPartition<>(Instant.parse(start), Instant.parse(exclusifEnd), step, Arrays.asList(parts));
	}
	
}
