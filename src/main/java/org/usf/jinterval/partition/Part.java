package org.usf.jinterval.partition;

public interface Part<T> {
	
	int getStartIndex();
	
	int getExclusifEndIndex();
	
	T getModel();

}
