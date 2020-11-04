package org.usf.learn.core.partition;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PartitionUtils {
	
	public static final int[] EMPTY_ARRAY = {};
	
	public static void checkIndexSize(int indexSize, int dataSize) {
		if(indexSize > dataSize) {
			throw new IllegalArgumentException("indexSize > dataSize");
		}
	}

}
