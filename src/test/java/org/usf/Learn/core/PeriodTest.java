package org.usf.learn.core;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

public class PeriodTest extends IntervalTest {

	static Stream<Arguments> caseFactory() {
		 return Stream.of(
    		Arguments.arguments(ofPeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 5), ChronoUnit.DAYS)),
    		Arguments.arguments(ofPeriod(LocalTime.of(0 , 40), LocalTime.of(0, 55), ChronoUnit.MINUTES)),
    		Arguments.arguments(ofPeriod(LocalDateTime.of(2020, 10, 25, 0 , 40), LocalDateTime.of(2020, 10, 25, 15, 40), ChronoUnit.HOURS)),
    		Arguments.arguments(ofPeriod(Instant.parse("2020-03-28T02:00:00Z"), Instant.parse("2020-03-28T02:01:00Z"), ChronoUnit.SECONDS))
	    );
	}

	@SuppressWarnings("unchecked")
	private static <T extends Comparable<? super T> & Temporal> IntervalImpl<T> ofPeriod(T start, T exclusifEnd, ChronoUnit temporalUnit){
		
		return new IntervalImpl<>(start, exclusifEnd, (o,v)->(T) o.plus(v, temporalUnit));
	}

}
