package org.usf.jinterval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Consumer;

import org.junit.jupiter.api.function.Executable;

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

}
