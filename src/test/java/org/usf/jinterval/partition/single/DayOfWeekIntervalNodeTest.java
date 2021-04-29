package org.usf.jinterval.partition.single;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DayOfWeekIntervalNodeTest {
	

	@ParameterizedTest(name = "{1})")
	@MethodSource("caseFactory")
	<M> void test(SingleModelPartition<M> expected, List<DayOfWeekIntervalNode<String>> nodes) {

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
							new SingleModelPart<>(0, 36, "MODEL")),
    				Arrays.asList(
    						new DayOfWeekIntervalNode<>("MODEL", FRIDAY, THURSDAY, Arrays.asList(
    								new DayOfWeekIntervalNode<>("MODEL", FRIDAY, THURSDAY, null),
									new DayOfWeekIntervalNode<>("MODEL", FRIDAY, THURSDAY, null)))))
			);
	    
	}
    				
	
	@SafeVarargs
	static <T> SingleModelPartition<T> expected(String start, String exclusifEnd, int step, SingleModelPart<T> ... parts) {
		
		return new SingleModelPartition<>(Instant.parse(start), Instant.parse(exclusifEnd), step, Arrays.asList(parts));
	}
	
}
