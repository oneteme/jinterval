package org.usf.learn.node;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ModelFullPartitionMap<M> {
	
	private final List<ModelFullPartition<M>> partitions;

}
