package org.usf.Learn.core;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.usf.learn.core.Period;

import lombok.Getter;

public class PeriodTest extends IntervalTest {

	static Stream<Arguments> caseFactory() {
		 return Stream.of(
    		Arguments.arguments(new PeriodImpl<>(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 5), ChronoUnit.DAYS)),
    		Arguments.arguments(new PeriodImpl<>(LocalTime.of(0 , 40), LocalTime.of(0, 55), ChronoUnit.MINUTES)),
    		Arguments.arguments(new PeriodImpl<>(LocalDateTime.of(2020, 10, 25, 0 , 40), LocalDateTime.of(2020, 10, 25, 15, 40), ChronoUnit.HOURS)),
    		Arguments.arguments(new PeriodImpl<>(Instant.parse("2020-03-28T02:00:00Z"), Instant.parse("2020-03-28T02:01:00Z"), ChronoUnit.SECONDS))
	    );
	}
	
	@Getter
	static class PeriodImpl<T extends Comparable<? super T> & Temporal> extends IntervalImpl<T> implements Period<T> {
		
		private final ChronoUnit temporalUnit;

		@SuppressWarnings("unchecked")
		public PeriodImpl(T start, T exclusifEnd, ChronoUnit temporalUnit) {
			super(start, exclusifEnd, (o,v)->(T) o.plus(v, temporalUnit));
			this.temporalUnit = temporalUnit;
		}
		
		@Override
		PeriodImpl<T> create(T s, T e){
			return new PeriodImpl<>(s, e, temporalUnit);
		}
	}

}
