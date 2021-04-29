package org.usf.jinterval.util;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtils {

	public static int requiredSameSize(Collection<? extends Collection<?>> c) {
		return requiredSameIntField(c, Collection::size);
	}

	public static <T> int requiredSameIntField(Collection<T> c, ToIntFunction<? super T> fn) {
		var it = requiredNotEmpty(c).iterator();
		int v = requireNonNull(fn).applyAsInt(it.next());
		while(it.hasNext()) {
			if(v != fn.applyAsInt(it.next())) {
				throw new IllegalArgumentException("not equals");
			}
		}
		return v;
	}

	public static <T, U> U requiredSameField(Collection<T> c, Function<T, U> fn) {
		var it = requiredNotEmpty(c).iterator();
		U v = requireNonNull(fn).apply(it.next());
		while(it.hasNext()) {
			if(!v.equals(fn.apply(it.next()))) {
				throw new IllegalArgumentException("not equals");
			}	
		}
		return v;
	}
	
	public static <C extends Collection<?>> C requiredNotEmpty(C c){
		if(requireNonNull(c).isEmpty()) {
			throw new IllegalArgumentException("empty list");
		}
		return c;
	}

	public static <T> List<T> requireNonNullElseEmptyList(List<T> list){
		return requireNonNullElseSupply(list, Collections::emptyList);
	}

	public static <T, C extends Collection<T>> C requireNonNullElseSupply(C list, Supplier<C> supp){
		return list == null ? supp.get() : list;
	}
}
