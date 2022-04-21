package org.usf.java.jinterval;

import static java.time.ZoneId.systemDefault;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.function.Executable;
import org.usf.java.jinterval.core.ImmutableInterval;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
	
	public static void assertExceptionMsg(Class<? extends Exception> expectedType, Executable executable, String expectedMessage) {
		
		assertException(expectedType, executable, e-> assertEquals(expectedMessage, e.getMessage()));
	}
	
	public static <E extends Exception> void assertException(Class<E> expectedType, Executable executable, Consumer<? super E> consumer) {

		consumer.accept(assertThrows(expectedType, executable));
	}

	public static void assertEmpty(Collection<?> collection) {
		
		assertTrue(collection.isEmpty());
	}
	
	@SafeVarargs
	public static <T extends Comparable<? super T>> List<ImmutableInterval<T>> intervals(T... values) {
		
		if(values == null || values.length % 2 > 0) {
			throw new IllegalArgumentException("array size");
		}
		List<ImmutableInterval<T>> list = new LinkedList<>();
		for(int i=1; i<values.length; i+=2) {
			list.add(new ImmutableInterval<>(values[i-1], values[i]));
		}
		return list;
	}

	public static <T extends Comparable<? super T>> ImmutableInterval<T> interval(T start, T exclusifEnd) {
		
		return new ImmutableInterval<>(start, exclusifEnd);
	}
	
	public static ZonedDateTime zdt(int y, int m, int d, int h) {
		
		return zdt(y, m, d, h, 0);
	}
	
	public static ZonedDateTime zdt(int y, int m, int d, int h, int mi) {
		
		return ZonedDateTime.of(LocalDate.of(y, m, d), LocalTime.of(h, mi), systemDefault());
	}
}
