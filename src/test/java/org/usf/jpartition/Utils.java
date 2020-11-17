package org.usf.jpartition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.function.Executable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
	
	public static void assertException(Class<? extends Exception> expectedType, Executable executable, String expectedMessage) {
		Exception e = assertThrows(expectedType, executable);
		assertEquals(expectedMessage, e.getMessage());
	}

}
