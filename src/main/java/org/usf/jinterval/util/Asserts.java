package org.usf.jinterval.util;

import java.util.Objects;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Asserts {
	
	public static final void assertEquals(int a, int b){
		if(a != b) {
			throw new IllegalArgumentException("not equals");
		}
	}

	public static final void assertEquals(Object a, Object b){
		if(!Objects.equals(a, b)) {
			throw new IllegalArgumentException("not equals");
		}
	}
	
	public static final int requirePositif(int v){

		return requirePositif(v, ()-> "Positif value required");
	}
	
	public static final int requirePositif(int v, Supplier<String> msg){
		if(v <= 0) {
			throw new IllegalArgumentException(msg.get());
		}
		return v;
	}
}
