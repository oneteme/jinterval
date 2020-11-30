package org.usf.jinterval.core;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.Month.MARCH;
import static java.time.Month.SEPTEMBER;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

interface IntervalFactory {	
	

	abstract <T extends Comparable<? super T>> Interval<T> create(T start, T exclusifEnd);
	
	
	default <T extends Comparable<? super T>> IntervalShiftingProxy<T> ofInterval(T start, T exclusifEnd, BiFunction<T, Integer, T> getFn){
		
		return new IntervalShiftingProxy<>(create(start, exclusifEnd), this::create, getFn);
	}
	
	static Stream<Arguments> numberIntervals() {
	    return Stream.of(
    		Arguments.arguments(1, 5, shiftInt),
    		Arguments.arguments(-5L, 1L, shiftLong),
    		Arguments.arguments(-30., -20., shiftDouble),
    		Arguments.arguments(ONE, TEN, shiftBigDecimal)
	    );
	}
	
	static Stream<Arguments> enumIntervals() { 
		 return Stream.of(
    		Arguments.arguments(MARCH, SEPTEMBER, shiftMonth),
    		Arguments.arguments(TUESDAY, FRIDAY, shiftDayOfWeek)
	    );
	}

	static Stream<Arguments> temporalIntervals() {
		 return Stream.of(
    		Arguments.arguments(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 5), shiftLocalDate),
    		Arguments.arguments(LocalTime.of(0 , 40), LocalTime.of(0, 55), shiftLocalTime),
    		Arguments.arguments(LocalDateTime.of(2020, 10, 25, 0 , 40), LocalDateTime.of(2020, 10, 25, 15, 40), shiftLocalDateTime),
    		Arguments.arguments(Instant.parse("2020-03-28T02:00:00Z"), Instant.parse("2020-03-28T02:00:05Z"), shiftInstant)
	    );
	}
	
 	static BinaryOperator<Integer> shiftInt = (a,b)-> a+b;
 	static BiFunction<Long, Integer, Long> shiftLong = (a,b)-> a+b;
 	static BiFunction<Double, Integer, Double> shiftDouble = (a,b)-> a+b;
 	static BiFunction<BigDecimal, Integer, BigDecimal> shiftBigDecimal = (a,b)-> a.add(BigDecimal.valueOf(b));
 	
 	static BiFunction<Month, Integer, Month> shiftMonth =(a,b)-> Month.values()[a.ordinal() + b];
 	static BiFunction<DayOfWeek, Integer, DayOfWeek> shiftDayOfWeek =(a,b)-> DayOfWeek.values()[a.ordinal() + b];
 	
 	static BiFunction<LocalDate, Integer, LocalDate> shiftLocalDate = (o,v)-> (LocalDate) o.plus(v, DAYS);
 	static BiFunction<LocalTime, Integer, LocalTime> shiftLocalTime = (o,v)-> (LocalTime) o.plus(v, MINUTES);
 	static BiFunction<LocalDateTime, Integer, LocalDateTime> shiftLocalDateTime = (o,v)-> (LocalDateTime) o.plus(v, HOURS);
 	static BiFunction<Instant, Integer, Instant> shiftInstant = (o,v)-> (Instant) o.plus(v, SECONDS);
	
}
