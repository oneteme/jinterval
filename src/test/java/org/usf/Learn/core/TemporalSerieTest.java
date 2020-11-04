package org.usf.Learn.core;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.usf.learn.core.serie.SimpleSerie;

class TemporalSerieTest {

	@ParameterizedTest(name="t={0} with step={2} {1}")
	@MethodSource("caseFactory")
	<T extends Temporal & Comparable<? super T>> void testConstructor(T t, ChronoUnit unit, int step) {
		assertException(NullPointerException.class, () -> {
	      new SimpleSerie<>(null, (short)0, null, null);
	    }, null);
	    assertException(RuntimeException.class, () -> {
	      new SimpleSerie<>(t, (short)-1, null, null);
	    }, "Positif value required");
	    assertException(RuntimeException.class, () -> {
	      new SimpleSerie<>(t, (short)0, null, null);
	    }, "Positif value required");
	    assertException(NullPointerException.class, () -> {
	      new SimpleSerie<>(t, step, null, null);
	    }, null);
	    assertException(NullPointerException.class, () -> {
	      new SimpleSerie<>(t, step, unit, null);
	    }, null);
	    new SimpleSerie<>(t, step, unit, Arrays.asList(1,2,3));
	}

	@ParameterizedTest(name="t={0} with step={2} {1}")
	@MethodSource("caseFactory")
	<T extends Temporal & Comparable<? super T>> void testExclusifEnd(T t, ChronoUnit unit, int step) {
		
		assertEquals(t, new SimpleSerie<>(t, step, unit, Collections.emptyList()).getExclusifEnd());
		assertEquals(t.plus(1*step, unit), new SimpleSerie<>(t, step, unit, Arrays.asList(4.5)).getExclusifEnd());
		assertEquals(t.plus(3*step, unit), new SimpleSerie<>(t, step, unit, Arrays.asList(null, null, null)).getExclusifEnd());
		assertEquals(t.plus(5*step, unit), new SimpleSerie<>(t, step, unit, Arrays.asList("DAY_1", "DAY_2", "DAY_3", "DAY_4", "DAY_5")).getExclusifEnd());
		assertEquals(t.plus(10*step, unit),new SimpleSerie<>(t, step, unit, Arrays.asList(0,1,2,3,4,5,6,7,8,9)).getExclusifEnd());
	}

	@ParameterizedTest(name="t={0} with step={2} {1}")
	@MethodSource("caseFactory")
	<T extends Temporal & Comparable<? super T>> void testInclusfiEnd(T t, ChronoUnit unit, int step) {

		assertEquals(t, new SimpleSerie<>(t, step, unit, Collections.emptyList()).inclusifEnd());
		assertEquals(t.plus(0*step, unit), new SimpleSerie<>(t, step, unit, Arrays.asList(4.5)).inclusifEnd());
		assertEquals(t.plus(2*step, unit), new SimpleSerie<>(t, step, unit, Arrays.asList(null, null, null)).inclusifEnd());
		assertEquals(t.plus(4*step, unit), new SimpleSerie<>(t, step, unit, Arrays.asList("DAY_1", "DAY_2", "DAY_3", "DAY_4", "DAY_5")).inclusifEnd());
		assertEquals(t.plus(9*step, unit), new SimpleSerie<>(t, step, unit, Arrays.asList(0,1,2,3,4,5,6,7,8,9)).inclusifEnd());
	}
	
	@SuppressWarnings("unchecked")
	@ParameterizedTest(name="t={0} with step={2} {1}")
	@MethodSource("caseFactory")
	<T extends Temporal & Comparable<? super T>> void testApply(T t, ChronoUnit unit, int step) {

		var s1 = new SimpleSerie<>(t, step, unit, Arrays.asList("DAY_", "DAY_", "DAY_", "DAY_", "DAY_"));
		var s2 = new SimpleSerie<>(t, step, unit, Arrays.asList(1,2,3,4,5));
		assertSerie(s1.apply(s2, (v1,v2)-> v1+v2), t, step, unit, Arrays.asList("DAY_1", "DAY_2", "DAY_3", "DAY_4", "DAY_5"));
		assertSerie(s2.apply(s1, (v1,v2)-> v2+v1), t, step, unit, Arrays.asList("DAY_1", "DAY_2", "DAY_3", "DAY_4", "DAY_5"));

		var s3 = new SimpleSerie<>(t, step, unit, Arrays.asList(0,1,2,3,4,5,6,7,8,9));
		var s4 = new SimpleSerie<>((T)t.plus(5*step, unit), step, unit, Arrays.asList(5));
		assertSerie(s3.apply(s4, (v1,v2)-> v1-v2), t.plus(5*step, unit), step, unit, Arrays.asList(0));
		assertSerie(s4.apply(s3, (v1,v2)-> v2-v1), t.plus(5*step, unit), step, unit, Arrays.asList(0));
		
		var s5 = new SimpleSerie<>(t, step, unit, Arrays.asList("z","y","x","w","v","u"));
		var s6 = new SimpleSerie<>((T)t.plus(3*step, unit), step, unit, Arrays.asList("a","b","c","d","e","f","g"));
		assertSerie(s5.apply(s6, (v1,v2)-> v1+v2), t.plus(3*step, unit), step, unit, Arrays.asList("wa","vb","uc"));
		assertSerie(s6.apply(s5, (v1,v2)-> v2+v1), t.plus(3*step, unit), step, unit, Arrays.asList("wa","vb","uc"));

		var s7 = new SimpleSerie<>(t, step, unit, Arrays.asList(1,2,3,4,5));
		var s8 = new SimpleSerie<T, Integer>(t, step, unit, Collections.emptyList());
		var s9 = new SimpleSerie<T, Integer>((T)t.plus(4*step, unit), step, unit, Collections.emptyList());
		assertSerie(s7.apply(s8, (v1,v2)-> v1+v2), t, step, unit, Collections.emptyList());
		assertSerie(s8.apply(s7, (v1,v2)-> v2+v1), t, step, unit, Collections.emptyList());
		assertSerie(s7.apply(s9, (v1,v2)-> v1+v2), t.plus(4*step, unit), step, unit, Collections.emptyList());
		assertSerie(s9.apply(s7, (v1,v2)-> v2+v1), t.plus(4*step, unit), step, unit, Collections.emptyList());
		
		var s10 = new SimpleSerie<>(t, step, unit, Arrays.asList(1,2,3));
		var s11 = new SimpleSerie<>(t, step, ChronoUnit.values()[unit.ordinal()+1], Arrays.asList());
		var s12 = new SimpleSerie<>(t, step+1, unit, Arrays.asList());
		var s13 = new SimpleSerie<>((T)t.plus(3*step, unit), step, unit, Arrays.asList());
		assertException(IllegalArgumentException.class, ()-> s10.apply(s11, (v1,v2)-> null), "not equals");
		assertException(IllegalArgumentException.class, ()-> s10.apply(s12, (v1,v2)-> null), "not equals");
		assertException(IllegalArgumentException.class, ()-> s10.apply(s13, (v1,v2)-> null), "Unconnected series");
		assertException(IllegalArgumentException.class, ()-> s13.apply(s10, (v1,v2)-> null), "Unconnected series");
		
		step = step % 2 == 0 ? step : step * 2;
		var s14 = new SimpleSerie<>((T)t.minus(step/2, unit), step, unit, Arrays.asList("DAY_", "DAY_", "DAY_", "DAY_", "DAY_"));
		var s15 = new SimpleSerie<>(t, step, unit, Arrays.asList(1,2,3,4,5));
		assertException(IllegalArgumentException.class, ()-> s14.apply(s15, (v1,v2)-> null), "distinct series");
		assertException(IllegalArgumentException.class, ()-> s15.apply(s14, (v1,v2)-> null), "distinct series");
	}
	
	void assertSerie(SimpleSerie<?,?> serie, Temporal t, int step, ChronoUnit unit, Collection<Object> collection) {

		assertEquals(t, serie.getStart());
		assertEquals(step, serie.getStep());
		assertEquals(unit, serie.getTemporalUnit());
		assertArrayEquals(collection.toArray(), serie.getPoints().toArray());
	}

	void assertException(Class<? extends Exception> expectedType, Executable executable, String expectedMessage) {
		Exception e = assertThrows(expectedType, executable);
		assertEquals(expectedMessage, e.getMessage());
	}
	
	private static Stream<Arguments> caseFactory() {
	    return Stream.of(
    		Arguments.of(LocalDate.of(2020, 1, 1), MONTHS, 1),
    		Arguments.of(LocalDate.of(2020, 10, 24), DAYS, 1),
    		Arguments.of(LocalDate.of(2020, 03, 28), DAYS, 5),
    		Arguments.of(LocalTime.of(07, 00), HOURS, 2),
    		Arguments.of(LocalTime.of(10, 40), MINUTES, 10),
    		Arguments.of(Instant.parse("2020-10-25T00:00:00Z"), MINUTES, 10),
    		Arguments.of(Instant.parse("2020-03-29T00:30:00Z"), SECONDS, 1800)
	    );
	}

}
