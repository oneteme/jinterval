package org.usf.jinterval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.function.Executable;
import org.usf.jinterval.core.ImmutableInterval;

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
}
