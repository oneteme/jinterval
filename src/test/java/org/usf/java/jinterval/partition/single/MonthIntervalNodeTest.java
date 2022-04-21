package org.usf.java.jinterval.partition.single;

import static java.time.Month.APRIL;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.SEPTEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.java.jinterval.Utils.zdt;
import static org.usf.java.jinterval.core.ImmutableInterval.regularInterval;

import java.time.Month;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class MonthIntervalNodeTest {


	@Test
	void testAdjustInterval_in() {
		
		testAdjustInterval(JANUARY, JANUARY, zdt(2022, 1, 1, 0), zdt(2022, 1, 1, 0), zdt(2023, 1, 1, 0)); //fullInterval first
		testAdjustInterval(JANUARY, JANUARY, zdt(2022, 6, 5, 3), zdt(2022, 1, 1, 0), zdt(2023, 1, 1, 0)); //fullInterval middle
		testAdjustInterval(JANUARY, JANUARY, zdt(2022, 12, 31, 23, 59), zdt(2022, 1, 1, 0), zdt(2023, 1, 1, 0)); //fullInterval last

		testAdjustInterval(APRIL, APRIL, zdt(2022, 1, 1, 0), zdt(2021, 4, 1, 0), zdt(2022, 4, 1, 0)); //fullInterval before
		testAdjustInterval(APRIL, APRIL, zdt(2022, 6, 5, 3), zdt(2022, 4, 1, 0), zdt(2023, 4, 1, 0)); //fullInterval 
		testAdjustInterval(APRIL, APRIL, zdt(2022, 12, 31, 23, 59), zdt(2022, 4, 1, 0), zdt(2023, 4, 1, 0));
		
		testAdjustInterval(APRIL, JANUARY, zdt(2022, 4, 1, 0), zdt(2022, 4, 1, 0), zdt(2023, 1, 1, 0));
		testAdjustInterval(APRIL, JANUARY, zdt(2022, 6, 5, 3), zdt(2022, 4, 1, 0), zdt(2023, 1, 1, 0));
		testAdjustInterval(APRIL, JANUARY, zdt(2022, 12, 31, 23, 59), zdt(2022, 4, 1, 0), zdt(2023, 1, 1, 0));

		testAdjustInterval(JANUARY, APRIL, zdt(2022, 1, 1, 0), zdt(2022, 1, 1, 0), zdt(2022, 4, 1, 0));
		testAdjustInterval(JANUARY, APRIL, zdt(2022, 2, 5, 3), zdt(2022, 1, 1, 0), zdt(2022, 4, 1, 0));
		testAdjustInterval(JANUARY, APRIL, zdt(2022, 3, 31, 23, 59), zdt(2022, 1, 1, 0), zdt(2022, 4, 1, 0));
	}

	@Test
	void testAdjustInterval_out() {

		testAdjustInterval(JANUARY, APRIL, zdt(2022, 4, 1, 0), zdt(2023, 1, 1, 0), zdt(2023, 4, 1, 0));
		testAdjustInterval(JANUARY, APRIL, zdt(2022, 6, 5, 3), zdt(2023, 1, 1, 0), zdt(2023, 4, 1, 0));
		testAdjustInterval(JANUARY, APRIL, zdt(2022, 12, 31, 23, 59), zdt(2023, 1, 1, 0), zdt(2023, 4, 1, 0));
		
		testAdjustInterval(APRIL, SEPTEMBER, zdt(2022, 1, 1, 0), zdt(2022, 4, 1, 0), zdt(2022, 9, 1, 0));
		testAdjustInterval(APRIL, SEPTEMBER, zdt(2022, 2, 5, 3), zdt(2022, 4, 1, 0), zdt(2022, 9, 1, 0));
		testAdjustInterval(APRIL, SEPTEMBER, zdt(2022, 3, 31, 23, 59), zdt(2022, 4, 1, 0), zdt(2022, 9, 1, 0));
		
		testAdjustInterval(APRIL, FEBRUARY, zdt(2022, 2, 1, 0), zdt(2022, 4, 1, 0), zdt(2023, 2, 1, 0));
		testAdjustInterval(APRIL, FEBRUARY, zdt(2022, 3, 31, 23, 59), zdt(2022, 4, 1, 0), zdt(2023, 2, 1, 0));
	}

	private static void testAdjustInterval(Month start, Month endExclusive, ZonedDateTime zdt, ZonedDateTime es, ZonedDateTime ee) {
		
		var in = new MonthIntervalNode<String>("", start, endExclusive).adjustInterval(zdt);
		assertEquals(in, regularInterval(es, ee));
	}
	
}
