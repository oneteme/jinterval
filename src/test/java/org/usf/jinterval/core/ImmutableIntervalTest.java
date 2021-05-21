package org.usf.jinterval.core;

import static java.math.BigDecimal.valueOf;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalTime.MIN;
import static java.time.LocalTime.NOON;
import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.MARCH;
import static java.time.Month.NOVEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.jinterval.Utils.assertExceptionMsg;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class ImmutableIntervalTest {

	@Test
	void testContainsField() {
		var v1 =new ImmutableInterval<>(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)); //regular
		assertTrue(v1.containsField(LocalDate.of(2020, 1, 1)));
		assertTrue(v1.containsField(LocalDate.of(2020, 1, 15)));
		assertFalse(v1.containsField(LocalDate.of(2020, 1, 31)));
		assertFalse(v1.containsField(LocalDate.of(2019, 12, 31)));

		var v2 = new ImmutableInterval<>(4, 2); //inverted
		assertTrue(v2.containsField(4));
		assertTrue(v2.containsField(1));
		assertFalse(v2.containsField(2));
		assertFalse(v2.containsField(3));

		var v3 =new ImmutableInterval<>(LocalTime.of(0, 0), LocalTime.of(0, 0)); //full
		assertTrue(v3.containsField(LocalTime.of(0, 0)));
		assertTrue(v3.containsField(LocalTime.of(12, 0)));
	}

	@Test
	void testContainsInterval() {
		var v1 =new ImmutableInterval<>(5.5, 6.44); //regular
		assertContainsInterval(v1, 5.5, 6.44, true);
		assertContainsInterval(v1, 5.95, 6.11, true);
		assertContainsInterval(v1, 4.49, 6.3, false);
		assertContainsInterval(v1, 6.45, 6.77, false);
		assertContainsInterval(v1, 4.23, 2.66, false);

		var v2 =new ImmutableInterval<>(AUGUST, MARCH); //inverted
		assertContainsInterval(v2, AUGUST, MARCH, true);
		assertContainsInterval(v2, NOVEMBER, JANUARY, true);
		assertContainsInterval(v2, NOVEMBER, DECEMBER, true);
		assertContainsInterval(v2, JANUARY, FEBRUARY, true);

		assertContainsInterval(v2, JANUARY, JANUARY, false);
		assertContainsInterval(v2, AUGUST, APRIL, false);
		assertContainsInterval(v2, JULY, NOVEMBER, false);
		assertContainsInterval(v2, JANUARY, APRIL, false);

		var v3 =new ImmutableInterval<>(SUNDAY, SUNDAY); //full
		assertContainsInterval(v3, SUNDAY, SUNDAY, true);
		assertContainsInterval(v3, MONDAY, FRIDAY, true);
	}

	private <T extends Comparable<T>> void assertContainsInterval(Interval<T> val, T o1, T o2, boolean expected) {
		assertEquals(expected, val.containsInterval(o1, o2));
		assertEquals(expected, val.containsInterval(new ImmutableInterval<>(o1, o2)));
		assertEquals(expected, new ImmutableInterval<>(o1, o2).inInterval(val));
		assertNotEquals(expected, new ImmutableInterval<>(o1, o2).notInInterval(val));
	}

	@Test
	void testIntersectInterval() {
		var v1 =new ImmutableInterval<>(valueOf(200.44), valueOf(552.15)); //regular
		assertIntersectInterval(v1, valueOf(200.44), valueOf(552.15), true);
		assertIntersectInterval(v1, valueOf(552.14), valueOf(614.79), true);
		assertIntersectInterval(v1, valueOf(152.78), valueOf(200.45), true);
		assertIntersectInterval(v1, valueOf(700.77), valueOf(200.45), true);
		assertIntersectInterval(v1, valueOf(552.14), valueOf(120.56), true);
		assertIntersectInterval(v1, valueOf(552.15), valueOf(668.14), false);
		assertIntersectInterval(v1, valueOf(152.78), valueOf(200.44), false);
		assertIntersectInterval(v1, valueOf(552.15), valueOf(200.44), false);

		var v2 =new ImmutableInterval<>(522l, -522l); //inverted
		assertIntersectInterval(v2, 522l, -522l, true);
		assertIntersectInterval(v2, 100l, -100l, true);
		assertIntersectInterval(v2, 677l, 521l, true);
		assertIntersectInterval(v2, -521l, -677l, true);
		assertIntersectInterval(v2, 0l, 0l, true);

		var v3 =new ImmutableInterval<>(LocalTime.of(0, 0), LocalTime.of(0, 0)); //full
		assertIntersectInterval(v3, LocalTime.of(12, 0), LocalTime.of(22, 0), true);
		assertIntersectInterval(v3, LocalTime.of(19, 0), LocalTime.of(8, 0), true);
	}

	private <T extends Comparable<T>> void assertIntersectInterval(Interval<T> val, T o1, T o2, boolean expected) {
		assertEquals(expected, val.intersectInterval(o1, o2));
		assertEquals(expected, val.intersectInterval(new ImmutableInterval<>(o1, o2)));
	}

	@Test
	void testIsRegular() {
		assertTrue(new ImmutableInterval<>(1, 5).isRegular());
		assertFalse(new ImmutableInterval<>(5, -5).isRegular());
		assertFalse(new ImmutableInterval<>(0, 0).isRegular());
	}

	@Test
	void testIsInverted() {
		assertTrue(new ImmutableInterval<>(5, -5).isInverted());
		assertTrue(new ImmutableInterval<>(0, 0).isInverted());
		assertFalse(new ImmutableInterval<>(1, 5).isInverted());
	}

	@Test
	void testSymmetrical() {
		assertTrue(new ImmutableInterval<>(LocalTime.of(7, 0), LocalTime.of(22, 30)).symmetrical(new ImmutableInterval<>(LocalTime.of(22, 30), LocalTime.of(7, 0))));
		assertTrue(new ImmutableInterval<>(LocalTime.of(0, 0), LocalTime.of(0, 0)).symmetrical(new ImmutableInterval<>(LocalTime.of(0, 0), LocalTime.of(0, 0))));
		assertFalse(new ImmutableInterval<>(LocalTime.of(7, 0), LocalTime.of(22, 30)).symmetrical(new ImmutableInterval<>(LocalTime.of(22, 00), LocalTime.of(7, 0))));
		assertFalse(new ImmutableInterval<>(LocalTime.of(7, 0), LocalTime.of(22, 30)).symmetrical(new ImmutableInterval<>(LocalTime.of(22, 30), LocalTime.of(7, 10))));
	}

	@Test
	void testEquals() {

		var v1 = new ImmutableInterval<>(LocalTime.MIN, LocalTime.NOON);
		assertTrue(v1.equals(v1));
		assertTrue(v1.equals(new ImmutableInterval<>(LocalTime.of(00, 00), LocalTime.of(12, 00))));
		assertFalse(v1.equals(null));
		assertFalse(v1.equals(new Object()));
		assertFalse(v1.equals(new ImmutableInterval<>(LocalTime.of(00, 00, 01), LocalTime.NOON)));
		assertFalse(v1.equals(new ImmutableInterval<>(LocalTime.MIN, LocalTime.of(12, 00, 01))));
		assertFalse(v1.equals(new ImmutableInterval<>(LocalTime.NOON, LocalTime.MIN)));
	}

	@Test
	void testHashCode() {

		assertEquals(1012, new ImmutableInterval<>(1, 20).hashCode());
		assertEquals(1582, new ImmutableInterval<>(20, 1).hashCode());
		assertEquals(1218947851, new ImmutableInterval<>(LocalTime.MIN, LocalTime.NOON).hashCode());
		assertEquals(-867351113, new ImmutableInterval<>(LocalTime.NOON, LocalTime.MIN).hashCode());
	}

	@Test
	void testIntervalDirection() {
		assertTrue(new ImmutableInterval<>(1, 20).intervalDirection() > 0);
		assertTrue(new ImmutableInterval<>(20, 1).intervalDirection() < 0);
		assertEquals(0, new ImmutableInterval<>(20, 20).intervalDirection());
	}

	@Test
	void testReverseInterval() {
		assertEquals(new ImmutableInterval<>(20, 1), new ImmutableInterval<>(1, 20).reverseInterval());
		assertEquals(new ImmutableInterval<>(1, 20), new ImmutableInterval<>(20, 1).reverseInterval());
		assertEquals(new ImmutableInterval<>(20, 20), new ImmutableInterval<>(20, 20).reverseInterval());
	}

	@Test
	void testToStirng() {
		assertEquals("[1, 20[", new ImmutableInterval<>(1, 20).toString());
		assertEquals("[20, 1[", new ImmutableInterval<>(20, 1).toString());
		assertEquals("[20, 20[", new ImmutableInterval<>(20, 20).toString());

		assertEquals("[00:00, 12:00[", new ImmutableInterval<>(MIN, NOON).toString());
		assertEquals("[12:00, 00:00[", new ImmutableInterval<>(NOON, MIN).toString());
		assertEquals("[00:00, 00:00[", new ImmutableInterval<>(MIN, MIN).toString());
	}

	@Test
	void testRegularInterval() {

		assertEquals(new ImmutableInterval<>(1., 20.), ImmutableInterval.regularInterval(1., 20.));
		assertExceptionMsg(IllegalArgumentException.class, ()-> ImmutableInterval.regularInterval(20., 1.), "start >= exclusifEnd");
	}

}
