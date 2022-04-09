package org.usf.java.jinterval.partition.util;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.usf.java.jinterval.util.CollectionUtils.requireNonNullElseEmptyList;
import static org.usf.java.jinterval.util.CollectionUtils.requiredNotEmpty;
import static org.usf.java.jinterval.util.CollectionUtils.requiredSameField;
import static org.usf.java.jinterval.util.CollectionUtils.requiredSameSize;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

	@Test
	void testRequiredNotEmpty() {
		assertThrows(NullPointerException.class, ()-> requiredNotEmpty(null));
		assertThrows(IllegalArgumentException.class, ()-> requiredNotEmpty(emptyList()));
		assertDoesNotThrow(()-> requiredNotEmpty(asList("")));
	}
	

	@Test
	void testRequiredSameSize() {
		assertThrows(NullPointerException.class, ()-> requiredSameSize(null));
		assertThrows(IllegalArgumentException.class, ()-> requiredSameSize(emptyList()));
		assertEquals(1, requiredSameSize(asList(asList(""))));
		assertEquals(3, requiredSameSize(asList(asList(1, 2, 3), asList("aa", "bb", "cc"))));
		assertThrows(IllegalArgumentException.class, ()-> requiredSameSize(asList(asList(2, 3), asList("aa", "bb", "cc"), asList('a', 'b', 'c'))));

	}
	
	@Test
	void testRequiredFiels() {
		Function<String, String> sub = s-> s.substring(1, 3);
		assertThrows(NullPointerException.class, ()-> requiredSameField(null, sub));
		assertThrows(IllegalArgumentException.class, ()-> requiredSameField(emptyList(), sub));
		assertEquals("ab", requiredSameField(Arrays.asList("AabB"), sub));
		assertEquals("ab", requiredSameField(Arrays.asList("AabB", "aabb", "baba"), sub));
		assertThrows(IllegalArgumentException.class, ()-> requiredSameField(Arrays.asList("AabB", "aAbb", "baBa"), sub));
	}

	@Test
	void testRequireNonNullElseEmptyList() {
		assertEquals(0, requireNonNullElseEmptyList(null).size());
		assertEquals(0, requireNonNullElseEmptyList(emptyList()).size());
		assertEquals(1, requireNonNullElseEmptyList(Arrays.asList("")).size());
	}

}
