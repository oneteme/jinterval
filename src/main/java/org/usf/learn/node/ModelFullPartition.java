package org.usf.learn.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor 
public final class ModelFullPartition<M> {
	
	private final M model;
	private final int[][] partitions;
	
}