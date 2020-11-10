package org.usf.learn.util;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtils {
	
	public static final <T> int requiredSameIntField(Collection<T> c, ToIntFunction<? super T> fn) {
		int[] arr = requiredNotEmpty(c).stream().mapToInt(fn).distinct().toArray();
		if(arr.length != 1) {
			throw new IllegalArgumentException("required same field");
		}
		return arr[0];
	}

	public static final <T, U> U requiredSameField(Collection<T> c, Function<T, U> fn) {
		List<U> arr = requiredNotEmpty(c).stream().map(fn).distinct().collect(Collectors.toList());
		if(arr.size() != 1) {
			throw new IllegalArgumentException("required same field");
		}
		return arr.get(0);
	}
	
	public static final <C extends Collection<?>> C requiredNotEmpty(C c){
		if(requireNonNull(c).isEmpty()) {
			throw new IllegalArgumentException("empty list");
		}
		return c;
	}

}
