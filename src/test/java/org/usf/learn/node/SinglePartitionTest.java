package org.usf.learn.node;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SinglePartitionTest {
	
	@ParameterizedTest(name = "assing({0}, {1})")
	@MethodSource("caseFactory")
	void test(List<SinglePartition<String>> l1, List<SinglePartition<String>> l2, List<SinglePartition<String>> expected) {
		
		assertIterableEquals(expected, SinglePartition.assign(l1, l2));
	}

	private static Stream<Arguments> caseFactory() {
	    return Stream.of(
    		Arguments.of(
    			Arrays.asList(of(0,10, "A")), Arrays.asList(),  
    			Arrays.asList(of(0,10, "A"))
			),
    		Arguments.of(
    			Arrays.asList(), Arrays.asList(of(10,20, "a")),  
    			Arrays.asList(of(10,20, "a"))
			),
    		Arguments.of(
    			Arrays.asList(of(0,10, "A")), Arrays.asList(of(10,20,"a")),  
    			Arrays.asList(of(0,10, "A"), of(10,20, "a"))
			),
    		Arguments.of(
    			Arrays.asList(of(0,10, "A")), Arrays.asList(of(5,20,"a")),  
    			Arrays.asList(of(0,5, "A"), of(5,20, "a"))
			),
    		Arguments.of(
    			Arrays.asList(of(0,5, "A"), of(5,10, "A"), of(10,15,"A")), 
    			Arrays.asList(of(10,20, "a")),  
    			Arrays.asList(of(0,5, "A"), of(5,10, "A"), of(10,20, "a"))
			),
    		Arguments.of(
    			Arrays.asList(of(0,5, "A"), of(7,10, "A"), of(12,15,"A")), 
    			Arrays.asList(of(10,20, "a")),  
    			Arrays.asList(of(0,5, "A"), of(7,10, "A"), of(10,20, "a"))
			),
    		Arguments.of(
    			Arrays.asList(of(0,5, "A"), of(7,10, "A"), of(12,15,"B")), 
    			Arrays.asList(of(5,9, "a"), of(13,14, "b")),  
    			Arrays.asList(of(0,5, "A"), of(5,9, "a"), of(9,10, "A"), of(12,13, "B"), of(13,14, "b"), of(14,15,"B"))
			),
    		Arguments.of(
    			Arrays.asList(of(0,5, "A"), of(7,10, "A"), of(12,15,"B")), 
    			Arrays.asList(of(0,5, "a"), of(7,10, "b"), of(12,15,"c"), of(20,30,"c")), 
    			Arrays.asList(of(0,5, "a"), of(7,10, "b"), of(12,15,"c"), of(20,30,"c"))
			)
	    );
	}
	
	private static SinglePartition<String> of(int start, int exclusifEnd, String model){
		
		return new SinglePartition<>(start, exclusifEnd, model);
	}
	
}
