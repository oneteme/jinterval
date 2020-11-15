package org.usf.learn.node;

import java.util.Optional;
import java.util.function.BinaryOperator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor 
public final class PeriodPartition<M> {
	
	private final M model;
	private final int[][] partitions;
	
	
}