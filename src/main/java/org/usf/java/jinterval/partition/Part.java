package org.usf.java.jinterval.partition;

public interface Part<T> {
	
	int getStartIndex();
	
	int getEndExclusiveIndex();
	
	T getModel();

}
