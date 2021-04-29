package org.usf.jinterval.core;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;
import static java.time.LocalTime.NOON;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.jinterval.Utils.assertExceptionMsg;
import static org.usf.jinterval.core.IntervalUtils.direction;
import static org.usf.jinterval.core.IntervalUtils.requiredPositifDirection;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class IntervalUtilsTest {

	@Test
	void testDirection() {
		
		assertEquals(0, direction(MONDAY, MONDAY));
		assertEquals(5, direction(MONDAY, SATURDAY));
		assertEquals(-5, direction(SATURDAY, MONDAY));
		
		assertEquals(0, direction(20, 20));
		assertEquals(1, direction(5, 20));
		assertEquals(-1, direction(20, 5));
	}
	
	@Test
	void testRequiredPositifDirection() {
		
		assertEquals(30, requiredPositifDirection(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)));
		assertEquals(1, requiredPositifDirection(MIN, MAX));
		
		assertExceptionMsg(IllegalArgumentException.class, ()->requiredPositifDirection(20, 20), "start >= exclusifEnd");
		assertExceptionMsg(IllegalArgumentException.class, ()->requiredPositifDirection(20, 5), "start >= exclusifEnd");
	}
	

	@Test
	void testRequiredPositifDirection2() {
		
		assertDoesNotThrow(()-> requiredPositifDirection(Collections.emptyList()));
		assertDoesNotThrow(()-> requiredPositifDirection(Arrays.asList(
				new ImmutableInterval<>(5, 20),
				new ImmutableInterval<>(1, 2),
				new ImmutableInterval<>(-1, 1))));
		assertExceptionMsg(IllegalArgumentException.class, ()-> requiredPositifDirection(Arrays.asList(
				new ImmutableInterval<>(5, 20),
				new ImmutableInterval<>(-1, -2),
				new ImmutableInterval<>(-1, 1))), "start >= exclusifEnd");
	}


	@Test
	void testToStirng() {
		
		assertEquals("[1, 20[", IntervalUtils.toString(1, 20));
		assertEquals("[20, 1[", IntervalUtils.toString(20, 1));
		assertEquals("[20, 20[", IntervalUtils.toString(20, 20));

		assertEquals("[00:00, 12:00[", IntervalUtils.toString(MIN, NOON));
		assertEquals("[12:00, 00:00[", IntervalUtils.toString(NOON, MIN));
		assertEquals("[00:00, 00:00[", IntervalUtils.toString(MIN, MIN));
		
		assertEquals("[MONDAY, SATURDAY[", IntervalUtils.toString(new ImmutableInterval<>(MONDAY, SATURDAY)));
		assertEquals("[SATURDAY, MONDAY[", IntervalUtils.toString(new ImmutableInterval<>(SATURDAY, MONDAY)));
		assertEquals("[MONDAY, MONDAY[", IntervalUtils.toString(new ImmutableInterval<>(MONDAY, MONDAY)));
	}

}
